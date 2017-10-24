package org.radrso.workflow.actions;

import org.radrso.workflow.internal.function.Functions;
import org.radrso.workflow.internal.model.Next;
import org.radrso.workflow.internal.model.WorkflowInstanceInfo;
import org.radrso.workflow.function.Action;
import org.radrso.workflow.handler.FlowStepHandler;

import java.util.List;

/**
 * Created by Rao-Mengnan
 * on 2017/10/23.
 */
public class OnCompletion implements Action {

    private FlowStepHandler handler;
    private WorkflowInstanceInfo instance;

    public OnCompletion(FlowStepHandler handler, WorkflowInstanceInfo instanceInfo) {
        this.handler = handler;
        this.instance = instanceInfo;
    }

    @Override
    public void run() throws Exception {
        List<String> cursors = handler.getCursor(instance);
        for (String cursor : cursors) {
            List<Next> nextList = handler.transferTo(cursor, instance);
            Functions.submitNext().accept(nextList);
        }
    }
}
