package org.radrso.workflow.wfservice.executor.impl;

import org.radrso.plugins.requests.entity.ResponseCode;
import org.radrso.workflow.entity.model.WorkflowInstance;
import org.radrso.workflow.launcher.FlowLauncher;
import org.radrso.workflow.launcher.WorkflowLaunchers;
import org.radrso.workflow.base.Commander;
import org.radrso.workflow.resolvers.WorkflowResolver;
import org.radrso.workflow.entity.model.WorkflowResult;
import org.radrso.workflow.wfservice.executor.InstanceJobRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * Created by raomengnan on 17-1-14.
 * 只做启动工作流实例的任务
 */
@Component
public class InstanceJobRunnerImpl implements InstanceJobRunner {

    @Autowired
    private Commander commander;

    @Override
    public WorkflowResult startExecute(WorkflowResolver workflowResolver, boolean rerun) {

        //保证调用的幂等性
        WorkflowInstance instance = workflowResolver.getWorkflowInstance();
        if(instance != null && !rerun) {
            if (instance.getStatus().equals(WorkflowInstance.COMPLETED)
                    || instance.getStatus().equals(WorkflowInstance.INTERRUPTED)) {
                return new WorkflowResult(ResponseCode.HTTP_OK.code(),
                        String.format("workflow instance is %s, please rerun it", instance.getStatus()), null);
            }
        }

        FlowLauncher flowActionsExecutor = WorkflowLaunchers.getFlowAction(commander);
        String msg;
        if (rerun){
            flowActionsExecutor.restart(workflowResolver);
            msg = String.format("Workflow instance[%s] is restarting", instance.getInstanceId());
        }
        else {
            flowActionsExecutor.start(workflowResolver);
            msg = String.format("Workflow instance[%s] is requested to run", instance.getInstanceId());
        }

        String instanceStatus = workflowResolver.getWorkflowInstance().getStatus();
        return new WorkflowResult(ResponseCode.HTTP_REQUEST_CONTINUE.code(), msg, null);
    }

    @Override
    public boolean interrupt(String instanceId) {
        FlowLauncher executor = WorkflowLaunchers.getFlowAction(commander);
        return executor.interrupt(instanceId);
    }


}
