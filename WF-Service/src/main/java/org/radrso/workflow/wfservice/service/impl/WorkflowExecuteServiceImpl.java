package org.radrso.workflow.wfservice.service.impl;

import org.radrso.plugins.requests.entity.exceptions.ResponseCode;
import org.radrso.workflow.entities.wf.WorkflowInstance;
import org.radrso.workflow.rmi.WorkflowInstanceExecutor;
import org.radrso.workflow.resolvers.WorkflowResolver;
import org.radrso.workflow.entities.response.WFResponse;
import org.radrso.workflow.wfservice.service.WorkflowExecuteService;
import org.radrso.workflow.wfservice.service.WorkflowExecuteStatusService;
import org.radrso.workflow.wfservice.service.WorkflowInstanceService;
import org.radrso.workflow.wfservice.subscribe.StepAction;
import org.radrso.workflow.wfservice.subscribe.WorkflowObservable;
import org.radrso.workflow.wfservice.subscribe.impl.StepActionImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by raomengnan on 17-1-14.
 */
@Service
public class WorkflowExecuteServiceImpl implements WorkflowExecuteService {

    @Autowired
    private WorkflowInstanceExecutor workflowInstanceExecutor;

    @Autowired
    private WorkflowExecuteStatusService workflowExecuteStatusService;

    @Autowired
    private WorkflowInstanceService workflowInstanceService;

    @Override
    public WFResponse execute(WorkflowResolver workflowResolver) {
        StepAction stepAction = new StepActionImpl(workflowInstanceService, workflowInstanceExecutor, workflowExecuteStatusService);

        WorkflowObservable.subscribe(stepAction, workflowResolver);

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
