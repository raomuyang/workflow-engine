package org.radrso.workflow.handler;

import org.radrso.workflow.resolvers.WorkflowResolver;

/**
 * Created by raomengnan on 17-1-17.
 * 递归遍历所有的步骤节点
 * 自动执行工作流
 */
public abstract class FlowLauncher {

    public abstract void start(WorkflowResolver workflowResolver);

    public abstract boolean interrupt(String instanceId);

    public abstract void restart(WorkflowResolver workflowResolver);
}
