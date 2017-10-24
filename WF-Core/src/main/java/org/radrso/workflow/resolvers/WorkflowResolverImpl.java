package org.radrso.workflow.resolvers;

import lombok.extern.log4j.Log4j;
import org.radrso.workflow.constant.EngineConstant;
import org.radrso.workflow.entities.StatusEnum;
import org.radrso.workflow.entities.schema.WorkflowSchema;
import org.radrso.workflow.entities.schema.items.Switch;
import org.radrso.workflow.entities.schema.items.Step;
import org.radrso.workflow.entities.schema.items.Transfer;
import org.radrso.workflow.entities.exceptions.ConfigReadException;
import org.radrso.workflow.entities.exceptions.UnknownExceptionInRunning;
import org.radrso.workflow.entities.model.WorkflowResult;
import org.radrso.workflow.entities.model.StepProcess;
import org.radrso.workflow.entities.model.WorkflowInstance;

import java.io.Serializable;
import java.util.*;


/**
 * Created by raomengnan on 17-1-14.
 */
@Log4j
public class WorkflowResolverImpl implements WorkflowResolver, Serializable {

    private WorkflowInstance workflowInstance;
    private SchemaResolver paramsResolver;

    private Step lastStep;
    private Transfer lastTransfer;
    private Step currentStep;
    private List<Transfer> currentBranches;

    private Map<String, Step> stepMap;
    // 用于准确统计分支次数
    private Set<String> branchesPool;

    private WorkflowResolverImpl() {
        this.stepMap = new HashMap<>();
    }

    public WorkflowResolverImpl(WorkflowSchema workflowConfig, WorkflowInstance workflowInstance) {
        this();
        this.workflowInstance = workflowInstance;
        this.paramsResolver = Resolvers.getParamsResolver(workflowInstance);
        this.branchesPool = new HashSet<>();

        if (workflowConfig.getSteps() != null)
            workflowConfig.getSteps().forEach(step -> {

                StepProcess stepProcess = new StepProcess(workflowInstance.getInstanceId(), step.getSign(), step.getName());
                stepProcess.setStatus(StatusEnum.WAIT);
                workflowInstance.getStepStatusesMap().put(step.getSign(), stepProcess);

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
    public WorkflowResolverImpl next() throws ConfigReadException, UnknownExceptionInRunning {
//        Transfer currentTransfer = getCurrentTransfer();
//        if (currentTransfer == null)
//            return null;
//
//        Step nextStep = transferToNextStep(currentTransfer);
//
//        //修改instance中每个step对应的状态
//        workflowInstance.getStepProcess().put(currentStep.getSign(), Step.FINISHED);
//        workflowInstance.getStepStatusesMap().get(currentStep.getSign()).setStatus(Step.FINISHED);
//
//        lastStep = currentStep;
//        currentStep = nextStep;
//        workflowInstance.getStepProcess().put(currentStep.getSign(), Step.RUNNING);
//        workflowInstance.getStepStatusesMap().get(currentStep.getSign()).setStatus(Step.RUNNING);

        return this;
    }

    /**
     * 回滚到上一步
     *
     * @return
     */
    @Override
    public WorkflowResolverImpl rollback() {
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
        if (getCurrentStep().getSign().equals(EngineConstant.SCHEMA_FINISH_SIGN))
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
    public Step transferToNextStep(Transfer transfer) throws ConfigReadException, UnknownExceptionInRunning {
        if (transfer == null)
            return null;

        if (transfer.getRunSwitch() == null) {
            getScatterBranches(transfer);
            paramsResolver.resolverTransferParams(transfer);
            lastTransfer = transfer;
            return stepMap.get(transfer.getTo());
        }

        Transfer nextTransfer = selectNextTransfer(transfer.getRunSwitch());
        return transferToNextStep(nextTransfer);
    }

    /**
     * 通过判断函数获取下一个要转移的的状态
     *
     * @param aSwitch
     * @return
     * @throws ConfigReadException
     */
    @Override
    public Transfer selectNextTransfer(Switch aSwitch) throws ConfigReadException, UnknownExceptionInRunning {
        log.debug(aSwitch);
        String type = aSwitch.getType();

        Object computeA = aSwitch.getVariable();
        Object computeAParse = paramsResolver.resolverStringToParams(computeA.toString());
        computeA = paramsResolver.parseValue(type, computeAParse);

        Object computeB = aSwitch.getCompareTo();
        Object computeBParse = paramsResolver.resolverStringToParams(computeB.toString());
        computeB = paramsResolver.parseValue(type, computeBParse);

        Comparable a = (Comparable) computeA;
        Comparable b = (Comparable) computeB;

        String condition = aSwitch.getExpression();
        switch (condition) {
            case ">":
                return (a.compareTo(b) > 0) ? aSwitch.getIfTransfer() : aSwitch.getElseTransfer();
            case "=":
            case "==":
                return (a.compareTo(b) == 0) ? aSwitch.getIfTransfer() : aSwitch.getElseTransfer();
            case "<":
                return (a.compareTo(b) < 0) ? aSwitch.getIfTransfer() : aSwitch.getElseTransfer();
            case ">=":
                return (a.compareTo(b) >= 0) ? aSwitch.getIfTransfer() : aSwitch.getElseTransfer();
            case "<=":
                return (a.compareTo(b) <= 0) ? aSwitch.getIfTransfer() : aSwitch.getElseTransfer();
            case "&&":
                return ((Boolean) computeA && (Boolean) computeB) ? aSwitch.getIfTransfer() : aSwitch.getElseTransfer();
            case "||":
                return ((Boolean) computeA || (Boolean) computeB) ? aSwitch.getIfTransfer() : aSwitch.getElseTransfer();
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
            Step s = this.stepMap.get(EngineConstant.SCHEMA_START_SIGN);
            this.currentStep = s;
            if (currentStep != null)
                workflowInstance.getStepProcess().put(EngineConstant.SCHEMA_START_SIGN, Step.RUNNING);
        }

        return currentStep;
    }

    @Override
    public Transfer getCurrentTransfer() {
        if (this.currentStep == null)
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
    public void updateResponse(String sign, WorkflowResult response) {
        workflowInstance.getStepStatusesMap().get(sign).setResult(response);
    }

    @Override
    public void setCurrentStep(Step currentStep) {
        this.currentStep = currentStep;
    }
}
