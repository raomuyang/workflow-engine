package org.radrso.workflow.wfservice.service.impl;

import lombok.extern.java.Log;
import org.radrso.plugins.requests.entity.exceptions.ResponseCode;
import org.radrso.workflow.entities.config.items.Transfer;
import org.radrso.workflow.entities.exceptions.WFRuntimeException;
import org.radrso.workflow.entities.wf.WorkflowInstance;
import org.radrso.workflow.rmi.WorkflowInstanceExecutor;
import org.radrso.workflow.resolvers.WorkflowResolver;
import org.radrso.workflow.entities.config.items.Step;
import org.radrso.workflow.entities.exceptions.ConfigReadException;
import org.radrso.workflow.entities.response.WFResponse;
import org.radrso.workflow.wfservice.service.WorkflowExecuteService;
import org.radrso.workflow.wfservice.service.WorkflowExecuteStatusService;
import org.radrso.workflow.wfservice.service.WorkflowInstanceService;
import org.radrso.workflow.wfservice.utils.OneStepAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import java.util.Date;

/**
 * Created by raomengnan on 17-1-14.
 */
@Service
@Log
public class WorkflowExecuteServiceImpl implements WorkflowExecuteService {

    @Autowired
    private WorkflowInstanceExecutor workflowInstanceExecutor;

    @Autowired
    private WorkflowExecuteStatusService workflowExecuteStatusService;

    @Autowired
    private WorkflowInstanceService workflowInstanceService;

    @Override
    public WFResponse execute(WorkflowResolver workflowResolver) {
        Observable<WorkflowResolver> observable = Observable.just(workflowResolver);

        do {
            observable = observable.doOnNext(new OneStepAction(workflowInstanceExecutor, workflowExecuteStatusService));
        }while (!workflowResolver.eof());

        observable.observeOn(Schedulers.io()).subscribe(new Subscriber<WorkflowResolver>() {
            @Override
            public void onCompleted() {
                workflowResolver.getWorkflowInstance().setStatus(WorkflowInstance.COMPLETED);
                workflowInstanceService.save(workflowResolver.getWorkflowInstance());
            }

            @Override
            public void onError(Throwable throwable) {
                if(WorkflowInstance.EXCEPTION.equals(throwable.getMessage()))
                    workflowResolver.getWorkflowInstance().setStatus(WorkflowInstance.EXPIRED);
                else
                    workflowResolver.getWorkflowInstance().setStatus(WorkflowInstance.EXCEPTION);

                workflowInstanceService.save(workflowResolver.getWorkflowInstance());
            }

            @Override
            public void onNext(WorkflowResolver workflowResolver) {
                workflowInstanceService.save(workflowResolver.getWorkflowInstance());
            }
        });

        String instanceStatus = workflowResolver.getWorkflowInstance().getStatus();
        if(WorkflowInstance.RUNNING.equals(instanceStatus))
            return new WFResponse(ResponseCode.HTTP_REQUEST_CONTINUE.code(), "workflow instance running", null);

        if(WorkflowInstance.COMPLETED.equals(instanceStatus))
            return new WFResponse(ResponseCode.HTTP_OK.code(), "workflow instance complated", null);

        if(WorkflowInstance.EXPIRED.equals(instanceStatus))
            return new WFResponse(ResponseCode.HTTP_UNAUTHORIZED.code(), "workflow instance expired", null);

        else
            return new WFResponse(ResponseCode.HTTP_SERVICE_UNAVAILABLE.code(), "workflow instance exception", null);

    }
}
