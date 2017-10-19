package org.radrso.workflow.internal.exec;

import lombok.extern.log4j.Log4j;
import org.radrso.workflow.handler.FlowLauncher;
import org.radrso.workflow.base.Commander;
import org.radrso.workflow.internal.actions.*;
import org.radrso.workflow.resolvers.WorkflowResolver;

/**
 * 流程自动执行器
 * Created by rao-mengnan on 2017/3/29.
 */
@Log4j
public class FlowExecutorImpl extends FlowLauncher {
    private Commander commander;

    public FlowExecutorImpl(Commander commander) {
        this.commander = commander;
    }

    @Override
    public void start(WorkflowResolver flowResolver) {
        Actions.startStream(flowResolver, commander);
    }


    @Override
    public boolean interrupt(String instanceId) {
        return new Operations(commander).interruptInstanceProcess(instanceId);
    }

    @Override
    public void restart(WorkflowResolver workflowResolver) {
        log.info("[RESTARTING] ---------- " + workflowResolver.getWorkflowInstance().getInstanceId());
        Actions.restartStream(workflowResolver, commander);
    }

}
