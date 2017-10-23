package org.radrso.workflow.launcher;

import lombok.extern.log4j.Log4j;
import org.radrso.workflow.internal.Operations;
import org.radrso.workflow.base.Commander;
import org.radrso.workflow.actions.*;
import org.radrso.workflow.resolvers.WorkflowResolver;

/**
 * 流程自动执行器
 * Created by rao-mengnan on 2017/3/29.
 */
@Log4j
public class FlowLauncherImpl extends FlowLauncher {
    private Commander commander;

    public FlowLauncherImpl(Commander commander) {
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
