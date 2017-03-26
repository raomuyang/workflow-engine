package org.radrso.workflow.resolvers.impl;

import lombok.extern.log4j.Log4j;
import org.radrso.workflow.ConfigConstant;
import org.radrso.workflow.entities.config.WorkflowConfig;
import org.radrso.workflow.entities.config.items.Judge;
import org.radrso.workflow.entities.config.items.Step;
import org.radrso.workflow.entities.config.items.Transfer;
import org.radrso.workflow.entities.exceptions.ConfigReadException;
import org.radrso.workflow.entities.exceptions.UnknowExceptionInRunning;
import org.radrso.workflow.entities.response.WFResponse;
import org.radrso.workflow.entities.wf.StepStatus;
import org.radrso.workflow.entities.wf.WorkflowInstance;
import org.radrso.workflow.resolvers.BaseParamsResolver;
import org.radrso.workflow.resolvers.BaseWorkflowConfigResolver;
import org.radrso.workflow.resolvers.ResolverChain;

import java.io.Serializable;
import java.util.*;


/**
 * Created by raomengnan on 17-1-14.
 */
@Log4j
public class WorkflowConfigResolver implements BaseWorkflowConfigResolver, Serializable {

    private WorkflowInstance workflowInstance;
    private BaseParamsResolver paramsResolver;

    private Step lastStep;
    private Transfer lastTransfer;
    private Step currentStep;
    private List<Transfer> currentBranches;

    private Map<String, Step> stepMap;
    // 用于准确统计分支次数
    private Set<String> branchesPool;

    private WorkflowConfigResolver() {
        this.stepMap = new HashMap<>();
    }

    public WorkflowConfigResolver(WorkflowConfig workflowConfig, WorkflowInstance workflowInstance) {
        this();
        this.workflowInstance = workflowInstance;
        this.paramsResolver = ResolverChain.getParamsResolver(workflowInstance);
        this.branchesPool = new HashSet<>();

        if (workflowConfig.getSteps() != null)
            workflowConfig.getSteps().forEach(step -> {

                StepStatus stepStatus = new StepStatus(step.getSign(), step.getName());
                stepStatus.setStatus(Step.WAIT);
                workflowInstance.getStepStatusesMap().put(step.getSign(), stepStatus);

                stepMap.put(step.getSign(), step);
                workflowInstance.getStepProcess().put(step.getSign(), Step.WAIT);
            });
    }

    /**
     * 工作流程向下个状态转移一次:
     * 当前状态 -- > 当前转移函数 -- > 下一个转移状态 + 分支状态
     *
     * @return 当前的resolver
     * @throws ConfigReadException
     */
    @Override
    public WorkflowConfigResolver next() throws ConfigReadException, UnknowExceptionInRunning {
        Transfer currentTransfer = getCurrentTransfer();
        if (currentTransfer == null)
            return null;

        Step nextStep = transferToNextStep(currentTransfer);

        //修改instance中每个step对应的状态
        workflowInstance.getStepProcess().put(currentStep.getSign(), Step.FINISHED);
        workflowInstance.getStepStatusesMap().get(currentStep.getSign()).setStatus(Step.FINISHED);

        lastStep = currentStep;
        currentStep = nextStep;
        workflowInstance.getStepProcess().put(currentStep.getSign(), Step.RUNNING);
        workflowInstance.getStepStatusesMap().get(currentStep.getSign()).setStatus(Step.RUNNING);

        return this;
    }

    /**
     * 回滚到上一步
     *
     * @return
     */
    @Override
    public WorkflowConfigResolver rollback() {
        currentStep = lastStep;
        return this;
    }

    /**
     * @return 当前是否为最后一步
     */
    @Override
    public boolean eof() {
        Step step = getCurrentStep();
        if (step == null)
            return true;
        if (getCurrentStep().getSign().equals(ConfigConstant.CONF_FINISH_SIGN))
            return true;
        return false;
    }

    /**
     * 从分支列表中取出一条
     * get and remove
     *
     * @return
     */
    @Override
    public Transfer popBranchTransfer() {
        if (this.currentBranches == null || this.currentBranches.size() == 0)
            return null;
        return this.currentBranches.remove(0);
    }


    /**
     * 状态转移函数到下一个状态的转换
     * 若没有判断函数，则直接转移到状态转移函数定义的下一个状态以及分支状态
     * 若有判断函数，判断函数 --> 状态转移函数 --> 下一状态
     *
     * @param transfer
     * @return
     * @throws ConfigReadException
     */
    @Override
    public Step transferToNextStep(Transfer transfer) throws ConfigReadException, UnknowExceptionInRunning {
        if (transfer == null)
            return null;

        if (transfer.getJudge() == null) {
            getScatterBranches(transfer);
            paramsResolver.resolverTransferParams(transfer);
            lastTransfer = transfer;
            return stepMap.get(transfer.getTo());
        }

        Transfer nextTransfer = judgeNextTransfer(transfer.getJudge());
        return transferToNextStep(nextTransfer);
    }

    /**
     * 通过判断函数获取下一个要转移的的状态
     *
     * @param judge
     * @return
     * @throws ConfigReadException
     */
    @Override
    public Transfer judgeNextTransfer(Judge judge) throws ConfigReadException, UnknowExceptionInRunning {
        log.debug(judge);
        String type = judge.getType();

        Object computeA = judge.getCompute();
        Object computeAParse = paramsResolver.resolverStringToParams(computeA.toString());
        computeA = paramsResolver.parseValue(type, computeAParse);

        Object computeB = judge.getComputeWith();
        Object computeBParse = paramsResolver.resolverStringToParams(computeB.toString());
        computeB = paramsResolver.parseValue(type, computeBParse);

        Comparable a = (Comparable) computeA;
        Comparable b = (Comparable) computeB;

        String condition = judge.getExpression();
        switch (condition) {
            case ">":
                return (a.compareTo(b) > 0) ? judge.getPassTransfer() : judge.getNopassTransfer();
            case "=":
            case "==":
                return (a.compareTo(b) == 0) ? judge.getPassTransfer() : judge.getNopassTransfer();
            case "<":
                return (a.compareTo(b) < 0) ? judge.getPassTransfer() : judge.getNopassTransfer();
            case ">=":
                return (a.compareTo(b) >= 0) ? judge.getPassTransfer() : judge.getNopassTransfer();
            case "<=":
                return (a.compareTo(b) <= 0) ? judge.getPassTransfer() : judge.getNopassTransfer();
            case "&&":
                return ((Boolean) computeA && (Boolean) computeB) ? judge.getPassTransfer() : judge.getNopassTransfer();
            case "||":
                return ((Boolean) computeA || (Boolean) computeB) ? judge.getPassTransfer() : judge.getNopassTransfer();
            default:
                throw new ConfigReadException("Unknow expression: " + condition);
        }
    }


    @Override
    public WorkflowInstance getWorkflowInstance() {
        return this.workflowInstance;
    }

    @Override
    public Step getCurrentStep() {
        if (currentStep == null && stepMap.size() > 0) {
            Step s = this.stepMap.get(ConfigConstant.CONF_START_SIGN);
            this.currentStep = s;
            if (currentStep != null)
                workflowInstance.getStepProcess().put(ConfigConstant.CONF_START_SIGN, Step.RUNNING);
        }

        return currentStep;
    }

    @Override
    public Transfer getCurrentTransfer() {
        if (getCurrentStep() == null)
            return null;
        else {
            return currentStep.getTransfer();
        }
    }

    /**
     * 通过sign-params 的映射返回当前状态的入参
     *
     * @return
     */
    @Override
    public Object[] getCurrentStepParams() {
        return workflowInstance.getStepStatusesMap().get(getCurrentStep().getSign()).getParams();
    }

    @Override
    public String[] getCurrentStepParamNames() {
        return workflowInstance.getStepStatusesMap().get(getCurrentStep().getSign()).getParamNames();
    }

    /**
     * @param transfer
     * @return 非空的一个List
     */
    @Override
    public List<Transfer> getScatterBranches(Transfer transfer) {
        int len = workflowInstance.getBranches();
        String currentStepSign = currentStep.getSign();
        List<Transfer> scatterBranches = new ArrayList<>();

        int branchNum = 1;
        if (transfer.getScatters() != null) {
            for (Transfer bTran : transfer.getScatters()) {
                scatterBranches.add(bTran);
                workflowInstance.getBranchStepMap().put(len + branchNum, currentStepSign);
                this.branchesPool.add(currentStepSign + branchNum);
                branchNum++;
            }
        }
        this.currentBranches = scatterBranches;
        this.workflowInstance.setBranches(branchesPool.size());
        return scatterBranches;
    }

    @Override
    public Step getLastStep() {
        return this.lastStep;
    }

    @Override
    public Transfer getLastTransfer() {
        return this.lastTransfer;
    }

    @Override
    public void updateResponse(String sign, WFResponse response) {
        workflowInstance.getStepStatusesMap().get(sign).setWfResponse(response);
    }

    @Override
    public void setCurrentStep(Step currentStep) {
        this.currentStep = currentStep;
    }
}
