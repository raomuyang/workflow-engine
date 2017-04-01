package org.radrso.workflow.wfservice.executor.impl;

import org.radrso.plugins.requests.entity.exceptions.ResponseCode;
import org.radrso.workflow.entities.config.WorkflowConfig;
import org.radrso.workflow.entities.wf.WorkflowInstance;
import org.radrso.workflow.exec.BaseFlowActionsExecutor;
import org.radrso.workflow.exec.FlowActonExecutorChain;
import org.radrso.workflow.persistence.BaseWorkflowSynchronize;
import org.radrso.workflow.resolvers.BaseWorkflowConfigResolver;
import org.radrso.workflow.entities.response.WFResponse;
import org.radrso.workflow.resolvers.ResolverChain;
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
    private BaseWorkflowSynchronize workflowSynchronize;

    @Override
    public WFResponse startExecute(BaseWorkflowConfigResolver workflowResolver, boolean rerun) {

        //保证调用的幂等性
        WorkflowInstance instance = workflowResolver.getWorkflowInstance();
        if(instance != null && !rerun) {
            if (instance.getStatus().equals(WorkflowInstance.COMPLETED)
                    || instance.getStatus().equals(WorkflowInstance.INTERRUPTED)) {
                return new WFResponse(ResponseCode.HTTP_OK.code(),
                        String.format("workflow instance is %s, please rerun it", instance.getStatus()), null);
            }
        }

        BaseFlowActionsExecutor flowActionsExecutor = FlowActonExecutorChain.getFlowAction(workflowSynchronize);
        String msg;
        if (rerun){
            flowActionsExecutor.restart(workflowResolver);
            msg = String.format("Workflow instance[%s] is restarting", instance.getInstanceId());
        }
        else {
            flowActionsExecutor.execute(workflowResolver);
            msg = String.format("Workflow instance[%s] is requested to run", instance.getInstanceId());
        }

        String instanceStatus = workflowResolver.getWorkflowInstance().getStatus();
        return new WFResponse(ResponseCode.HTTP_REQUEST_CONTINUE.code(), msg, null);
    }

    @Override
    public boolean interrupt(String instanceId) {
        BaseFlowActionsExecutor executor = FlowActonExecutorChain.getFlowAction(workflowSynchronize);
        return executor.interruptInstanceExec(instanceId);
    }


}
