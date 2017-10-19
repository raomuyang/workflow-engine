package org.radrso.workflow.executor;

import org.radrso.workflow.resolvers.FlowResolver;

/**
 * Created by raomengnan on 17-1-17.
 * 递归遍历所有的步骤节点
 * 自动执行工作流
 */
public abstract class FlowExecutor {

    public abstract void start(FlowResolver workflowResolver);

    public abstract boolean interrupt(String instanceId);

    public abstract void restart(FlowResolver workflowResolver);
}
