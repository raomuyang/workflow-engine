package org.radrso.workflow.internal.exec.actions;

import org.radrso.workflow.base.Commander;
import org.radrso.workflow.constant.ExceptionCode;
import org.radrso.workflow.entities.exceptions.WFRuntimeException;
import org.radrso.workflow.internal.exec.FlowExecuteStream;
import org.radrso.workflow.resolvers.FlowResolver;

/**
 * Created by rao-mengnan on 2017/5/18.
 */
public class Actions {
    public static Action getAction(ActionEnum action, Commander workflowSynchronize) {
        switch (action) {
            case INTERRUPT_AND_CHECK:
                return new InterruptAndCheckActon(workflowSynchronize);
            case ON_EXEC_NEXT:
                return new OnExecuteNextAction(null, workflowSynchronize);
            case ON_STEP_COMPLETED:
                return new OnStepCompletedAction(workflowSynchronize);
            case ON_STEP_ERROR:
                return new OnStepErrorAction(workflowSynchronize, null);
            case ON_STEP_EXEC:
                return new OnStepExecAction(workflowSynchronize);
            default:
                throw new WFRuntimeException("Action not found", ExceptionCode.UNKNOW.code());
        }
    }

    public static void startStream(FlowResolver workflowConfigResolver, Commander workflowSynchronize) {
        new FlowExecuteStream(workflowConfigResolver, workflowSynchronize).process();
    }

}
