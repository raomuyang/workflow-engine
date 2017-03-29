package org.radrso.workflow.exec;

import org.radrso.workflow.exec.base.FlowActionsExecutor;
import org.radrso.workflow.persistence.BaseWorkflowSynchronize;

/**
 * Created by rao-mengnan on 2017/3/29.
 * 提供BaseFlowAction的实现
 */
public class FlowActonExecutorChain {
    public static FlowActionsExecutor getFlowAction(BaseWorkflowSynchronize workflowSynchronize){
        return new FlowActionsExecutor(workflowSynchronize);
    }
}
