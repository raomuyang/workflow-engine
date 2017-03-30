package org.radrso.workflow.exec.base;

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
import org.radrso.workflow.exec.BaseFlowActionsExecutor;
import org.radrso.workflow.exec.FlowActonExecutorChain;
import org.radrso.workflow.persistence.BaseWorkflowSynchronize;
import org.radrso.workflow.resolvers.BaseWorkflowConfigResolver;
import org.radrso.workflow.resolvers.ResolverChain;
import rx.Observable;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import java.util.Date;
import java.util.Map;

/**
 * 流程自动执行器
 * Created by rao-mengnan on 2017/3/29.
 */
@Log4j
public class FlowActionsExecutor extends BaseFlowActionsExecutor{
    private BaseWorkflowSynchronize workflowSynchronize;

    public FlowActionsExecutor(BaseWorkflowSynchronize workflowSynchronize) {
        this.workflowSynchronize = workflowSynchronize;
    }

    @Override
    public boolean interruptInstanceExec(String instanceId) {
        log.info("[INTERRUPT] " + instanceId);
        if (instanceId.contains("-")) {
            instanceId = instanceId.substring(0, instanceId.indexOf("-"));
        }
        WorkflowInstance instance = workflowSynchronize.getInstance(instanceId);
        if (instance == null){
            return false;
        }

        instance.setStatus(WorkflowInstance.INTERRUPTED);
        return workflowSynchronize.updateInstance(instance);
    }

    @Override
    public void restart(final BaseWorkflowConfigResolver workflowResolver) {
        log.info("[RESTARTING] ---------- " + workflowResolver.getWorkflowInstance().getInstanceId());
        InterruptAndCheckActon interruptAndCheckActon = new InterruptAndCheckActon();
        ExecuteAction executeAction = new ExecuteAction(workflowResolver);
        Observable.just(workflowResolver).doOnNext(interruptAndCheckActon).doOnCompleted(executeAction).subscribeOn(Schedulers.io()).subscribe();
    }

    @Override
    public void doOnStepCompleted(BaseWorkflowConfigResolver workflowResolver) {
        String instanceId = workflowResolver.getWorkflowInstance().getInstanceId();
        String stepName = workflowResolver.getCurrentStep().getName();

        log.info("[STEP-COMPLETED] " + instanceId + " " + stepName);

        // 只有在instance没有被中断的情况下才能继续执行下一步
        boolean stopped = checkIsInstanceInterrupted(workflowResolver.getWorkflowInstance().getInstanceId());
        if (stopped){
            log.info(String.format("[STEP-INTERRUPT] Instance %s is interrupted, (%s) status will discard", instanceId, stepName));
            WorkflowInstance originInfo = workflowSynchronize.getInstance(instanceId);
            if (originInfo == null){
                log.error(String.format("Check instance stopped: Can't find instance by [%s]", instanceId));
                return;
            }
            originInfo.setStatus(WorkflowInstance.INTERRUPTED);
            workflowSynchronize.updateInstance(originInfo);
            return;
        }

        String stepSign = workflowResolver.getCurrentStep().getSign();
        workflowResolver.getWorkflowInstance().getStepProcess().put(stepSign, Step.FINISHED);

        Map<String, StepStatus> stepStatusMap = workflowResolver.getWorkflowInstance().getStepStatusesMap();
        stepStatusMap.get(stepSign).setStatus(Step.FINISHED);
        workflowResolver.getWorkflowInstance().getFinishedSequence().add(
                workflowResolver.getCurrentStep().getSign()
        );

        boolean eof = workflowResolver.eof();
        if (eof) {
            workflowResolver.getWorkflowInstance().setStatus(WorkflowInstance.COMPLETED);
            workflowResolver.getWorkflowInstance().setSubmitTime(new Date());
        }

        workflowSynchronize.updateInstance(workflowResolver.getWorkflowInstance());

        if (!eof) {
            FlowActonExecutorChain.getFlowAction(workflowSynchronize).execute(workflowResolver);
        }
    }

    @Override
    public void doOnStepError(BaseWorkflowConfigResolver workflowResolver, Throwable throwable) {
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

            workflowResolver.getWorkflowInstance().getStepProcess().put(stepSign, Step.STOPPED);
            if (stepStatus != null)
                stepStatus.setStatus(Step.STOPPED);
            else
                log.error("StepStatus is null:" + stepSign);
        }
        workflowSynchronize.updateInstance(instance);

        ObjectId objectId = new ObjectId();
        String msg = throwable.getMessage();
        if (msg == null || msg.equals(""))
            msg = throwable.toString();
        int code = ResponseCode.UNKNOW.code();
        if (WFRuntimeException.class.isInstance(throwable))
            code = ((WFRuntimeException) throwable).getCode();
        WorkflowErrorLog errorLog = new WorkflowErrorLog(
                objectId.toHexString(),
                code,
                instance.getWorkflowId(),
                instance.getInstanceId(),
                workflowResolver.getCurrentStep().getSign(),
                msg,
                objectId.getDate(),
                throwable);
        workflowSynchronize.logError(errorLog);
    }

    @Override
    public void doNextStep(BaseWorkflowConfigResolver workflowResolver) {
        boolean loopDo = true;
        boolean isReloadJarFile = false;//判断是否已经重新加载jar文件

        while (loopDo) {
            // 在执行之前判断Instance是否被中断
            if (checkIsInstanceInterrupted(workflowResolver.getWorkflowInstance().getInstanceId())){
                return;
            }

            loopDo = false;

            verifyDate(workflowResolver);
            try {
                workflowResolver.next();
                Step step = workflowResolver.getCurrentStep();
                if (step != null) {
                    StepStatus stepStatus = workflowResolver.getWorkflowInstance().getStepStatusesMap().get(step.getSign());
                    if (stepStatus.getBegin() == null)
                        stepStatus.setBegin(new Date());
                }

                execBranches(workflowResolver);

                log.info("[STEP-START] " + workflowResolver.getWorkflowInstance().getInstanceId() + " " + step.getName() + String.format(" Thread[%s]", Thread.currentThread().getId()));
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
                            String instanceId = workflowResolver.getWorkflowInstance().getInstanceId();
                            WorkflowConfig workflowConfig = workflowSynchronize.getWorkflow(instanceId);

                            if (workflowConfig == null){
                                throw new WFRuntimeException("No such instance: " + instanceId, ResponseCode.ILLEGAL_ARGMENT_EXCEPTION.code());
                            }
                            if (workflowConfig != null && !BaseWorkflowSynchronize.isDefinedJarsFiles(workflowConfig))
                                throw new WFRuntimeException("No jars to load class:" + response.getMsg(), ResponseCode.JAR_FILE_NOT_FOUND.code());

                            //若使用RPC执行，最大重试次数为3
                            int retry = 3;
                            while (retry > 0) {
                                try {
                                    retry--;
                                    boolean isImported = workflowSynchronize.importJars(wfId);
                                    if (isImported)
                                        break;
                                    if (retry < 0)
                                        throw new WFRuntimeException("Jar files import failed", ResponseCode.JAR_FILE_NOT_FOUND.code());
                                } catch (RpcException e) {
                                    log.error(String.format("The %s times to try rpc:", 6 - retry) + e.getMessage());
                                    if (retry == 0)
                                        throw new WFRuntimeException("RPC invoke timeout[importJars]", ResponseCode.SOCKET_EXCEPTION.code());
                                }
                            }
                        } else if (ResponseCode.CLASS_NOT_FOUND.code() <= code && code <= ResponseCode.JAR_FILE_NOT_FOUND.code()) {
                            throw new WFRuntimeException(response.getMsg(), code);
                        }

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
            } catch (RuntimeException runtimeException) {
                log.error(runtimeException);
                if (WFRuntimeException.class.isInstance(runtimeException))
                    throw runtimeException;

                runtimeException.printStackTrace();
                throw new WFRuntimeException(runtimeException.getMessage(),
                        runtimeException, ResponseCode.UNKNOW.code());
            } catch (UnknowExceptionInRunning unknowExceptionInRunning) {
                log.error(unknowExceptionInRunning);
                unknowExceptionInRunning.printStackTrace();
                throw new WFRuntimeException(unknowExceptionInRunning.getMessage(),
                        unknowExceptionInRunning, ResponseCode.UNKNOW.code());
            } finally {
                if (!loopDo) {
                    String stepSign = workflowResolver.getCurrentStep().getSign();
                    workflowResolver.getWorkflowInstance().getStepStatusesMap().get(stepSign).setEnd(new Date());
                }
            }
        }
    }

    private void execBranches(BaseWorkflowConfigResolver workflowResolver) {
        Step currentStep = workflowResolver.getCurrentStep();
        // 获取分支的转移函数
        Transfer scatterTransfer = workflowResolver.popBranchTransfer();

        // 标记当前是第几个分支
        int num = 0;
        while (scatterTransfer != null) {
            int branchNum = workflowResolver.getWorkflowInstance().getBranches() - num;
            String branchId = workflowResolver.getWorkflowInstance().getInstanceId() + "-" + branchNum;
            num++;

            // 防止重复执行已经执行过的分支
            WorkflowInstance instance = workflowSynchronize.getInstance(branchId);
            if (instance != null && WorkflowInstance.COMPLETED.equals(instance.getStatus())){
                scatterTransfer = workflowResolver.popBranchTransfer();
                continue;
            }

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

            WorkflowInstance branchInstance = new WorkflowInstance(workflowConfig.getId(), branchId);
            BaseWorkflowConfigResolver newWFResolver = ResolverChain.getWorkflowConfigResolver(workflowConfig, branchInstance);

            Step tmpLastStep = new Step();
            tmpLastStep.setTransfer(scatterTransfer);
            tmpLastStep.setSign(workflowResolver.getLastStep().getSign());

            scatterTransfer = workflowResolver.popBranchTransfer();
            newWFResolver.setCurrentStep(tmpLastStep);
            FlowActonExecutorChain.getFlowAction(workflowSynchronize).execute(newWFResolver);
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
            throw new WFRuntimeException(WFRuntimeException.WORKFLOW_EXPIRED, ResponseCode.HTTP_FORBIDDEN.code());

    }

    private boolean checkWorkflowStatus(BaseWorkflowConfigResolver workflowResolver) {
        String status = workflowSynchronize.getWorkflowStatus(workflowResolver.getWorkflowInstance().getWorkflowId());
        if (status == null)
            throw new WFRuntimeException(WFRuntimeException.NO_SUCH_WORKFLOW_STATUS, ResponseCode.HTTP_NOT_FOUND.code());

        boolean available = WorkflowExecuteStatus.START.equals(status);
        if (!available)
            return false;
        return true;
    }

    /**
     * 同步instance状态，检查instance是否已经被中断
     * @param instanceId
     * @return
     */
    private boolean checkIsInstanceInterrupted(String instanceId){
        String mainInstance = instanceId;
        if (mainInstance.contains("-")){
            mainInstance = mainInstance.substring(0, mainInstance.indexOf("-"));
        }

        WorkflowInstance instance = workflowSynchronize.getInstance(mainInstance);
        if (instance == null){
            String msg = String.format("Check instance stopped: Can't find instance by [%s]", instanceId);
            log.error(msg);
            throw new WFRuntimeException(msg, ResponseCode.HTTP_NOT_FOUND.code());
        }

        if (instance.getStatus().endsWith(WorkflowInstance.INTERRUPTED)){
            return true;
        }
        return false;
    }

    /**
     * 中断和检查中断的操作
     */
    private class InterruptAndCheckActon implements Action1<BaseWorkflowConfigResolver>{
        @Override
        public void call(BaseWorkflowConfigResolver resolver) {
            String instanceId = resolver.getWorkflowInstance().getInstanceId();
            interruptInstanceExec(instanceId);

            boolean interrupted = false;
            WorkflowInstance originInstanceInfo = workflowSynchronize.getInstance(instanceId);
            int branches = originInstanceInfo.getBranches();
            long brockTime = 1000 * 60 * 5; // 超时时间为五分钟
            long beginTime = System.currentTimeMillis();

            /*当轮询检查所有的分支，直到没有分支处于RUNNING状态才退出检查*/
            while (!interrupted) {
                if (brockTime < System.currentTimeMillis() - beginTime) {
                    break;
                }

                if (originInstanceInfo.getStatus().equals(WorkflowInstance.RUNNING)){
                    originInstanceInfo =  workflowSynchronize.getInstance(instanceId);
                    continue;
                }

                interrupted = true;
                for (int i = 1; i <= branches; i++){
                    String id = instanceId + "-" + i;
                    WorkflowInstance instanceBranch = workflowSynchronize.getInstance(id);
                    if (instanceBranch.getStatus().equals(WorkflowInstance.RUNNING)) {
                        interrupted = false;
                        break;
                    }
                }
            }
            if (!interrupted){
                WorkflowErrorLog errorLog = new WorkflowErrorLog(
                        null,
                        ResponseCode.INTERRUPT_EXCEPTION.code(),
                        resolver.getWorkflowInstance().getWorkflowId(),
                        instanceId,
                        null,
                        "Interrupt timeout error",
                        new Date(),
                        null
                );
                workflowSynchronize.logError(errorLog);
                log.error(errorLog);
            }
            else {
                log.info("Interrupted: " + instanceId);
            }
        }
    }

    private class ExecuteAction implements Action0{
        public BaseWorkflowConfigResolver resolver;

        ExecuteAction(BaseWorkflowConfigResolver resolver){
            this.resolver = resolver;
        }
        @Override
        public void call() {
            WorkflowInstance instance = resolver.getWorkflowInstance();
            instance.setStatus(WorkflowInstance.RUNNING);
            workflowSynchronize.updateInstance(instance);
            execute(resolver);
        }
    }
}
