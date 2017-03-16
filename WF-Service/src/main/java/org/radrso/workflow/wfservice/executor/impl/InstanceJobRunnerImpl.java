package org.radrso.workflow.wfservice.executor.impl;

import org.radrso.plugins.requests.entity.exceptions.ResponseCode;
import org.radrso.workflow.entities.wf.WorkflowInstance;
import org.radrso.workflow.exec.StepExecutor;
import org.radrso.workflow.exec.base.impl.StepAction;
import org.radrso.workflow.resolvers.BaseWorkflowConfigResolver;
import org.radrso.workflow.entities.response.WFResponse;
import org.radrso.workflow.wfservice.executor.InstanceJobRunner;
import org.radrso.workflow.wfservice.executor.WorkflowSyncironze;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * Created by raomengnan on 17-1-14.
 * 只做启动工作流实例的任务
 */
@Component
public class InstanceJobRunnerImpl implements InstanceJobRunner {

    @Autowired
    private WorkflowSyncironze workflowSyncironze;

    @Override
    public WFResponse startExecute(BaseWorkflowConfigResolver workflowResolver) {
        //保证调用的幂等性
        WorkflowInstance instance = workflowResolver.getWorkflowInstance();
        if(instance != null)
            if(instance.getStatus().equals(WorkflowInstance.COMPLETED))
                return new WFResponse(ResponseCode.HTTP_OK.code(), "workflow instance complated", null);


        StepAction stepAction = new StepAction(workflowSyncironze);
        StepExecutor.execute(stepAction, workflowResolver);

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
