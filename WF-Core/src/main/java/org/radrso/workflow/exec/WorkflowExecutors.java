package org.radrso.workflow.exec;

import org.radrso.workflow.internal.exec.FlowExecutorImpl;
import org.radrso.workflow.base.Commander;

/**
 * Created by rao-mengnan on 2017/3/29.
 * 提供BaseFlowAction的实现
 */
public class WorkflowExecutors {
    public static FlowExecutor getFlowAction(Commander commander){
        return new FlowExecutorImpl(commander);
    }
}
