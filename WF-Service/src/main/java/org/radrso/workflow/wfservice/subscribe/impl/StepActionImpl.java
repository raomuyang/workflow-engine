package org.radrso.workflow.wfservice.subscribe.impl;

import lombok.extern.log4j.Log4j;
import org.bson.types.ObjectId;
import org.radrso.plugins.requests.entity.exceptions.ResponseCode;
import org.radrso.workflow.entities.config.items.Step;
import org.radrso.workflow.entities.config.items.Transfer;
import org.radrso.workflow.entities.exceptions.ConfigReadException;
import org.radrso.workflow.entities.exceptions.UnknowExceptionInRunning;
import org.radrso.workflow.entities.exceptions.WFRuntimeException;
import org.radrso.workflow.entities.response.WFResponse;
import org.radrso.workflow.entities.wf.WorkflowErrorLog;
import org.radrso.workflow.entities.wf.WorkflowExecuteStatus;
import org.radrso.workflow.entities.wf.WorkflowInstance;
import org.radrso.workflow.resolvers.WorkflowResolver;
import org.radrso.workflow.rmi.WorkflowInstanceExecutor;
import org.radrso.workflow.wfservice.service.WorkflowCommandService;
import org.radrso.workflow.wfservice.service.WorkflowExecuteStatusService;
import org.radrso.workflow.wfservice.service.WorkflowInstanceService;
import org.radrso.workflow.wfservice.service.WorkflowLogService;
import org.radrso.workflow.wfservice.subscribe.StepAction;

import java.util.Date;

/**
 * Created by raomengnan on 17-1-17.
 */
@Log4j
public class StepActionImpl implements StepAction{

    private WorkflowCommandService workflowCommandService;

    public StepActionImpl(WorkflowCommandService workflowCommandService) {
        this.workflowCommandService = workflowCommandService;
    }

    @Override
    public void stepCompleted(WorkflowResolver workflowResolver) {
        log.info("[STEP-COMPLETED] " + workflowResolver.getWorkflowInstance().getInstanceId() + " " + workflowResolver.getCurrentStep().getName());

        workflowResolver.getWorkflowInstance().getStepProcess().put(workflowResolver.getCurrentStep().getSign(), Step.FINISHED);
        if(workflowResolver.eof())
            workflowResolver.getWorkflowInstance().setStatus(WorkflowInstance.COMPLETED);

        workflowCommandService.updateInstance(workflowResolver.getWorkflowInstance());
    }

    @Override
    public void stepError(WorkflowResolver workflowResolver, Throwable throwable) {
        WorkflowInstance instance = workflowResolver.getWorkflowInstance();
        log.error("[STEP-EXCEPTION] " +   instance.getInstanceId() + " " + workflowResolver.getCurrentStep().getSign() + " " + throwable);
        if(WFRuntimeException.WORKFLOW_EXPIRED.equals(throwable.getMessage()))
            instance.setStatus(WorkflowInstance.EXPIRED);
        else
            instance.setStatus(WorkflowInstance.EXCEPTION);

        workflowCommandService.updateInstance(instance);

        ObjectId objectId = new ObjectId();
        WorkflowErrorLog log = new WorkflowErrorLog(
                objectId.toHexString(),
                instance.getWorkflowId(),
                instance.getInstanceId(),
                workflowResolver.getCurrentStep().getSign(),
                throwable.toString(),
                objectId.getDate(),
                throwable);
        workflowCommandService.logError(log);
    }

    @Override
    public void stepNext(WorkflowResolver workflowResolver) {
        boolean loopDo = true;
        while (loopDo) {
            loopDo = false;

//            verifyDate(workflowResolver);
            try {
                workflowResolver.next();
                Step step = workflowResolver.getCurrentStep();
                log.info("[START] " + workflowResolver.getWorkflowInstance().getInstanceId() + " " + step.getName() + String.format(" Thread[%s]", Thread.currentThread().getId()) );
                Object[] params = workflowResolver.getCurrentStepParams();
                String[] paramNames = workflowResolver.getCurrentStepParamNames();

                WFResponse response = null;
                if (step.getCall() != null)
                    response = workflowCommandService.execute(step, params, paramNames);

                if(response != null){
                    int code = response.getCode();

                    if(code == ResponseCode.HTTP_OK.code())
                        workflowResolver.putResponse(step.getSign(), response);

                    else {
                        //发生错误，回滚一步
                        workflowResolver.back();
                        if(code == ResponseCode.CLASS_NOT_FOUND.code())
                            workflowCommandService.importJars(workflowResolver.getWorkflowInstance().getWorkflowId());
                        else if (ResponseCode.CLASS_NOT_FOUND.code() < code && code < ResponseCode.JAR_FILE_NOT_FOUND.code())
                            throw new WFRuntimeException(response.getMsg());
                    }
                }
            } catch (ConfigReadException e) {
                System.out.println(e);
                loopDo = true;
                workflowResolver.back();
                try {
                    Thread.sleep(1000 * 60);
                } catch (InterruptedException e1) {
                    log.error(e1);
                }
            } catch (UnknowExceptionInRunning unknowExceptionInRunning) {
                log.error(unknowExceptionInRunning);
                unknowExceptionInRunning.printStackTrace();
                throw new WFRuntimeException(unknowExceptionInRunning.toString());
            }
        }
    }

    private void verifyDate(WorkflowResolver workflowResolver){
        Transfer lastTransfer = workflowResolver.getCurrentTransfer();
        Date diedline = null;
        boolean isContinue = true;
        if (lastTransfer != null && (diedline = lastTransfer.getDiedline()) != null)
            isContinue = new Date().before(diedline);

        isContinue = isContinue && checkWorkflowStatus(workflowResolver);
        if(! isContinue)
            throw new WFRuntimeException(WFRuntimeException.WORKFLOW_EXPIRED);

    }

    private boolean checkWorkflowStatus(WorkflowResolver workflowResolver){
        String status = workflowCommandService.getWFStatus(
                    workflowResolver.getWorkflowInstance()
                            .getWorkflowId());
        if(status == null)
            throw new WFRuntimeException(WFRuntimeException.NO_SUCH_WORKFLOW_STATUS);
        boolean isStart = WorkflowExecuteStatus.START.equals(status);
        //还没有验证工作流是否停止

        return true;
    }
}
