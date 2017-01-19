package org.radrso.workflow.wfservice.subscribe.impl;

import lombok.extern.log4j.Log4j;
import org.radrso.workflow.entities.config.items.Step;
import org.radrso.workflow.entities.config.items.Transfer;
import org.radrso.workflow.entities.exceptions.ConfigReadException;
import org.radrso.workflow.entities.exceptions.UnknowExceptionInRunning;
import org.radrso.workflow.entities.exceptions.WFRuntimeException;
import org.radrso.workflow.entities.response.WFResponse;
import org.radrso.workflow.entities.wf.WorkflowExecuteStatus;
import org.radrso.workflow.entities.wf.WorkflowInstance;
import org.radrso.workflow.resolvers.WorkflowResolver;
import org.radrso.workflow.rmi.WorkflowInstanceExecutor;
import org.radrso.workflow.wfservice.service.WorkflowExecuteStatusService;
import org.radrso.workflow.wfservice.service.WorkflowInstanceService;
import org.radrso.workflow.wfservice.subscribe.StepAction;

import java.util.Date;

/**
 * Created by raomengnan on 17-1-17.
 */
@Log4j
public class StepActionImpl implements StepAction{

    private WorkflowInstanceService workflowInstanceService;
    private WorkflowInstanceExecutor workflowInstanceExecutor;
    private WorkflowExecuteStatusService workflowExecuteStatusService;

    public StepActionImpl(WorkflowInstanceService workflowInstanceService, WorkflowInstanceExecutor workflowInstanceExecutor, WorkflowExecuteStatusService workflowExecuteStatusService) {
        this.workflowInstanceService = workflowInstanceService;
        this.workflowInstanceExecutor = workflowInstanceExecutor;
        this.workflowExecuteStatusService = workflowExecuteStatusService;
    }

    @Override
    public void stepCompleted(WorkflowResolver workflowResolver) {
        log.info("[STEP-COMPLETED] " + workflowResolver.getWorkflowInstance().getInstanceId() + " " + workflowResolver.getCurrentStep().getName());

        workflowResolver.getWorkflowInstance().getStepProcess().put(workflowResolver.getCurrentStep().getSign(), Step.FINISHED);
        if(workflowResolver.eof())
            workflowResolver.getWorkflowInstance().setStatus(WorkflowInstance.COMPLETED);

        workflowInstanceService.save(workflowResolver.getWorkflowInstance());
    }

    @Override
    public void stepError(WorkflowResolver workflowResolver, Throwable throwable) {
        log.error("[STEP-EXCEPTION] " +   workflowResolver.getWorkflowInstance().getInstanceId() + " " + throwable);
        if(WFRuntimeException.WORKFLOW_EXPIRED.equals(throwable.getMessage()))
            workflowResolver.getWorkflowInstance().setStatus(WorkflowInstance.EXPIRED);
        else
            workflowResolver.getWorkflowInstance().setStatus(WorkflowInstance.EXCEPTION);

        workflowInstanceService.save(workflowResolver.getWorkflowInstance());
    }

    @Override
    public void stepNext(WorkflowResolver workflowResolver) {
        boolean loopDo = true;
        while (loopDo) {
            loopDo = false;

            verifyDate(workflowResolver);
            try {
                workflowResolver.next();
                Step step = workflowResolver.getCurrentStep();
                log.info("[START] " + workflowResolver.getWorkflowInstance().getInstanceId() + " " + step.getName() + String.format(" Thread[%s]", Thread.currentThread().getId()) );
                Object[] params = workflowResolver.getCurrentStepParams();
                String[] paramNames = workflowResolver.getCurrentStepParamNames();

                WFResponse response = null;
                if (step.getCall() != null) {
                    response = workflowInstanceExecutor.execute(step, params, paramNames);
                    workflowResolver.putResponse(step.getSign(), response);
                }
            } catch (ConfigReadException e) {
                System.out.println(e);
                loopDo = true;
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
        if (lastTransfer != null && (diedline = lastTransfer.getDiedline()) != null) {
            boolean isContinue = new Date().before(diedline);
            isContinue = isContinue && checkWorkflowStatus(workflowResolver);
            if(! isContinue)
                throw new WFRuntimeException(WFRuntimeException.WORKFLOW_EXPIRED);
        }
    }

    private boolean checkWorkflowStatus(WorkflowResolver workflowResolver){
        String status = workflowExecuteStatusService.getStatus(
                    workflowResolver.getWorkflowInstance()
                            .getWorkflowId());
        if(status == null)
            throw new WFRuntimeException(WFRuntimeException.NO_SUCH_WORKFLOW_STATUS);
        boolean isStart = WorkflowExecuteStatus.START.equals(status);
        //还没有验证工作流是否停止

        return true;
    }
}
