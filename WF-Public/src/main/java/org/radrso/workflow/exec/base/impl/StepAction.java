package org.radrso.workflow.exec.base.impl;

import com.alibaba.dubbo.rpc.RpcException;
import lombok.extern.log4j.Log4j;
import org.bson.types.ObjectId;
import org.radrso.plugins.requests.entity.exceptions.ResponseCode;
import org.radrso.workflow.entities.config.WorkflowConfig;
import org.radrso.workflow.entities.config.items.Step;
import org.radrso.workflow.entities.config.items.Transfer;
import org.radrso.workflow.entities.exceptions.ConfigReadException;
import org.radrso.workflow.entities.exceptions.UnknowExceptionInRunning;
import org.radrso.workflow.entities.exceptions.WFRuntimeException;
import org.radrso.workflow.entities.response.WFResponse;
import org.radrso.workflow.entities.wf.StepStatus;
import org.radrso.workflow.entities.wf.WorkflowErrorLog;
import org.radrso.workflow.entities.wf.WorkflowExecuteStatus;
import org.radrso.workflow.entities.wf.WorkflowInstance;
import org.radrso.workflow.exec.base.BaseStepAction;
import org.radrso.workflow.exec.StepExecutor;
import org.radrso.workflow.persistence.BaseWorkflowSynchronize;
import org.radrso.workflow.resolvers.BaseWorkflowConfigResolver;
import org.radrso.workflow.resolvers.ResolverChain;

import java.util.Date;
import java.util.Map;

/**
 * Created by raomengnan on 17-1-17.
 */
@Log4j
public class StepAction implements BaseStepAction {

    private BaseWorkflowSynchronize workflowSynchronize;

    public StepAction(BaseWorkflowSynchronize workflowSynchronize) {
        this.workflowSynchronize = workflowSynchronize;
    }

    @Override
    public void stepCompleted(BaseWorkflowConfigResolver workflowResolver) {
        log.info("[STEP-COMPLETED] " + workflowResolver.getWorkflowInstance().getInstanceId() + " " + workflowResolver.getCurrentStep().getName());

        String stepSign = workflowResolver.getCurrentStep().getSign();
        workflowResolver.getWorkflowInstance().getStepProcess().put(stepSign, Step.FINISHED);

        Map<String, StepStatus> stepStatusMap = workflowResolver.getWorkflowInstance().getStepStatusesMap();
        stepStatusMap.get(stepSign).setStatus(Step.FINISHED);
        workflowResolver.getWorkflowInstance().getFinishedSequence().add(
                workflowResolver.getCurrentStep().getSign()
        );

        boolean eof = false;
        if (eof = workflowResolver.eof()) {
            workflowResolver.getWorkflowInstance().setStatus(WorkflowInstance.COMPLETED);
            workflowResolver.getWorkflowInstance().setSubmitTime(new Date());
        }

        workflowSynchronize.updateInstance(workflowResolver.getWorkflowInstance());
        if (!eof)
            StepExecutor.execute(this, workflowResolver);
    }

    @Override
    public void stepError(BaseWorkflowConfigResolver workflowResolver, Throwable throwable) {
        WorkflowInstance instance = workflowResolver.getWorkflowInstance();
        log.error("[STEP-EXCEPTION] " + instance.getInstanceId() + " " + workflowResolver.getCurrentStep().getSign() + " " + throwable);
        if (WFRuntimeException.WORKFLOW_EXPIRED.equals(throwable.getMessage()))
            instance.setStatus(WorkflowInstance.EXPIRED);
        else
            instance.setStatus(WorkflowInstance.EXCEPTION);

        Step currentStep = workflowResolver.getCurrentStep();
        if (currentStep != null) {
            String stepSign = workflowResolver.getCurrentStep().getSign();
            Map<String, StepStatus> stepStatusMap = workflowResolver.getWorkflowInstance().getStepStatusesMap();
            StepStatus stepStatus = stepStatusMap.get(stepSign);

            workflowResolver.getWorkflowInstance().getStepProcess().put(stepSign, Step.STOPED);
            if (stepStatus != null)
                stepStatus.setStatus(Step.STOPED);
            else
                log.error("StepStatus is null:" + stepSign);
        }
        workflowSynchronize.updateInstance(instance);

        ObjectId objectId = new ObjectId();
        String msg = throwable.getMessage();
        if (msg == null || msg.equals(""))
            msg = throwable.toString();
        WorkflowErrorLog errorLog = new WorkflowErrorLog(
                objectId.toHexString(),
                instance.getWorkflowId(),
                instance.getInstanceId(),
                workflowResolver.getCurrentStep().getSign(),
                msg,
                objectId.getDate(),
                throwable);
        workflowSynchronize.logError(errorLog);
    }

    @Override
    public void stepNext(BaseWorkflowConfigResolver workflowResolver) {
        boolean loopDo = true;
        boolean isReloadJarFile = false;//判断是否已经重新加载jar文件

        while (loopDo) {
            loopDo = false;

            verifyDate(workflowResolver);
            try {
                workflowResolver.next();
                Step step = workflowResolver.getCurrentStep();

                execBranchs(workflowResolver);

                log.info("[START] " + workflowResolver.getWorkflowInstance().getInstanceId() + " " + step.getName() + String.format(" Thread[%s]", Thread.currentThread().getId()));
                Object[] params = workflowResolver.getCurrentStepParams();
                String[] paramNames = workflowResolver.getCurrentStepParamNames();

                WFResponse response = null;
                if (step.getCall() != null)
                    response = workflowSynchronize.startStep(step, params, paramNames);

                if (response != null) {
                    int code = response.getCode();

                    if (code == ResponseCode.HTTP_OK.code())
                        workflowResolver.updateResponse(step.getSign(), response);

                    else {
                        loopDo = true;
                        if (code == ResponseCode.CLASS_NOT_FOUND.code() && !isReloadJarFile) {
                            isReloadJarFile = true;
                            String wfId = workflowResolver.getWorkflowInstance().getWorkflowId();
                            WorkflowConfig workflowConfig = workflowSynchronize.getWorkflow(workflowResolver.getWorkflowInstance().getInstanceId());
                            if (workflowConfig != null && !BaseWorkflowSynchronize.isDefinedJarsFiles(workflowConfig))
                                throw new WFRuntimeException("No jars to load class:" + response.getMsg());

                            //若使用RPC执行，最大重试次数为3
                            int retry = 3;
                            while (retry > 0) {
                                try {
                                    retry--;
                                    boolean isImported = workflowSynchronize.importJars(wfId);
                                    if (isImported)
                                        break;
                                    if (retry < 0)
                                        throw new WFRuntimeException("Jar files import failed");
                                } catch (RpcException e) {
                                    log.error(String.format("The %s times to try rpc:", 6 - retry) + e.getMessage());
                                    if (retry == 0)
                                        throw new WFRuntimeException("RPC invoke timeout[importJars]");
                                }
                            }
                        } else if (ResponseCode.CLASS_NOT_FOUND.code() <= code && code <= ResponseCode.JAR_FILE_NOT_FOUND.code())
                            throw new WFRuntimeException("[" + code + "]" + response.getMsg());

                        //发生错误时，完成错误鉴别后回滚一步
                        workflowResolver.rollback();
                    }
                }
            } catch (ConfigReadException e) {
                System.out.println(e);
                loopDo = true;
                workflowResolver.rollback();
                try {
                    Thread.sleep(1000 * 60);
                } catch (InterruptedException e1) {
                    log.error(e1);
                }
            } catch (UnknowExceptionInRunning unknowExceptionInRunning) {
                log.error(unknowExceptionInRunning);
                unknowExceptionInRunning.printStackTrace();
                throw new WFRuntimeException(unknowExceptionInRunning.getMessage(), unknowExceptionInRunning);
            } finally {
                if (!loopDo) {
                    String stepSign = workflowResolver.getCurrentStep().getSign();
                    workflowResolver.getWorkflowInstance().getStepStatusesMap().get(stepSign).setEnd(new Date());
                }
            }
        }
    }

    private void execBranchs(BaseWorkflowConfigResolver workflowResolver) {
        Step currentStep = workflowResolver.getCurrentStep();
        //获取分支的转移函数
        Transfer scatterTransfer = workflowResolver.popBranchTransfer();
//        Step scatterNextStep = workflowResolver.popBranchTransfer();

        //标记当前是第几个分支
        int num = 0;
        while (scatterTransfer != null) {
            String msg = String.format("Scatter to [%s] from step[%s]/[%s]",
                    scatterTransfer.getTo(), currentStep.getSign(), workflowResolver.getWorkflowInstance().getInstanceId());
            log.info(msg);

            // 克隆workflowConfig对象
            String instanceId = workflowResolver.getWorkflowInstance().getInstanceId();
            WorkflowConfig workflowConfig = workflowSynchronize.getWorkflow(instanceId);

            if (workflowConfig == null) {
                WorkflowErrorLog log = new WorkflowErrorLog();
                log.setMsg("Instance not found:" + msg);
                log.setWorkflowId(workflowResolver.getWorkflowInstance().getWorkflowId());
                log.setInstanceId(workflowResolver.getWorkflowInstance().getInstanceId());
                log.setStepSign(workflowResolver.getCurrentStep().getSign());
                workflowSynchronize.logError(log);
                continue;
            }

            WorkflowInstance branchInstance = new WorkflowInstance(workflowConfig.getId(), null);
            branchInstance.setInstanceId(
                    workflowResolver.getWorkflowInstance().getInstanceId() + "-" +
                            (workflowResolver.getWorkflowInstance().getBranches() - num++)
            );
            BaseWorkflowConfigResolver newWFResolver = ResolverChain.getWorkflowConfigResolver(workflowConfig, branchInstance);

//            //当前分支的上一个转移函数，用于获取input，deadline等
//            Transfer lastTranInMain = workflowResolver.getLastTransfer();

            //为不和主分支相互影响，克隆当前分支的上一个转移函数
//            Transfer virtualLastTransfer = new Transfer();
//            virtualLastTransfer.setDeadline(lastTranInMain.getDeadline());
//            virtualLastTransfer.setInput(lastTranInMain.getInput());
//            virtualLastTransfer.setTo(scatterNextStep.getSign());

            //用克隆的转移函数和主分支的步骤标志虚构分支的上一状态（Step），从而分支进入next()时可以调用克隆的转移函数
            Step tmpLastStep = new Step();
            tmpLastStep.setTransfer(scatterTransfer);
            tmpLastStep.setSign(workflowResolver.getLastStep().getSign());

            scatterTransfer = workflowResolver.popBranchTransfer();
            newWFResolver.setCurrentStep(tmpLastStep);
            StepExecutor.execute(this, newWFResolver);

        }
    }

    private void verifyDate(BaseWorkflowConfigResolver workflowResolver) {
        Transfer lastTransfer = workflowResolver.getCurrentTransfer();
        Date diedline = null;
        boolean isContinue = true;
        if (lastTransfer != null && (diedline = lastTransfer.getDeadline()) != null)
            isContinue = new Date().before(diedline);

        isContinue = isContinue && checkWorkflowStatus(workflowResolver);
        if (!isContinue)
            throw new WFRuntimeException(WFRuntimeException.WORKFLOW_EXPIRED);

    }

    private boolean checkWorkflowStatus(BaseWorkflowConfigResolver workflowResolver) {
        String status = workflowSynchronize.getWorkflowStatus(
                workflowResolver.getWorkflowInstance()
                        .getWorkflowId());
        if (status == null)
            throw new WFRuntimeException(WFRuntimeException.NO_SUCH_WORKFLOW_STATUS);
        // TODO 还没有验证工作流是否停止
        boolean isStart = WorkflowExecuteStatus.START.equals(status);
        if (!isStart)
            return false;
        return true;
    }
}
