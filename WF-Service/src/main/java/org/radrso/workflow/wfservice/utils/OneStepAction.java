package org.radrso.workflow.wfservice.utils;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.radrso.workflow.entities.config.items.Step;
import org.radrso.workflow.entities.config.items.Transfer;
import org.radrso.workflow.entities.exceptions.ConfigReadException;
import org.radrso.workflow.entities.exceptions.WFRuntimeException;
import org.radrso.workflow.entities.response.WFResponse;
import org.radrso.workflow.entities.wf.WorkflowExecuteStatus;
import org.radrso.workflow.entities.wf.WorkflowInstance;
import org.radrso.workflow.resolvers.WorkflowResolver;
import org.radrso.workflow.rmi.WorkflowInstanceExecutor;
import org.radrso.workflow.wfservice.service.WorkflowExecuteStatusService;
import rx.functions.Action1;

import java.util.Date;

/**
 * Created by raomengnan on 17-1-16.
 */
@AllArgsConstructor
@Log4j
public class OneStepAction implements Action1<WorkflowResolver> {
    private WorkflowInstanceExecutor workflowInstanceExecutor;
    private WorkflowExecuteStatusService workflowExecuteStatusService;

    @Override
    public void call(WorkflowResolver workflowResolver) {

        boolean loopDo = true;
        while (loopDo) {
            loopDo = false;

            verifyDate(workflowResolver);
            try {
                workflowResolver.next();
                Step step = workflowResolver.getCurrentStep();
                Object[] params = workflowResolver.getCurrentStepParams();
                String[] paramNames = workflowResolver.getCurrentStepParamNames();

                WFResponse response = null;
                if (step.getCall() != null) {
                    response = workflowInstanceExecutor.execute(step, params, paramNames);
                    workflowResolver.putResponse(step.getSign(), response);
                }
            } catch (ConfigReadException e) {
                loopDo = true;
                try {
                    Thread.sleep(1000 * 60);
                } catch (InterruptedException e1) {
                    log.error(e1);
                }
            }
        }

    }

    private void verifyDate(WorkflowResolver workflowResolver){
        Transfer lastTransfer = workflowResolver.getCurrentTransfer();
        Date diedline = null;
        if (lastTransfer != null && (diedline = lastTransfer.getDiedline()) != null) {
            boolean isContinue = new Date().before(diedline);
            isContinue = isContinue && workflowExecuteStatusService.getStatus(
                    workflowResolver.getWorkflowInstance()
                            .getApplicationId()).equals(WorkflowExecuteStatus.START);
            //还没有验证工作流是否停止
            if(! isContinue)
                throw new WFRuntimeException(WorkflowInstance.EXPIRED);
        }
    }
}
