package org.radrso.workflow.handler;

import org.radrso.workflow.internal.exec.FlowExecutorImpl;
import org.radrso.workflow.base.Commander;

/**
 * Created by rao-mengnan on 2017/3/29.
 * 提供BaseFlowAction的实现
 */
public class WorkflowLaunchers {
    public static FlowLauncher getFlowAction(Commander commander){
        return new FlowExecutorImpl(commander);
    }
}
