package org.radrso.workflow.exec.base;

import org.radrso.workflow.resolvers.WorkflowResolver;


/**
 * Created by raomengnan on 17-1-17.
 */

public interface BaseStepAction {
    void stepNext(WorkflowResolver workflowResolver);
    void stepError(WorkflowResolver workflowResolver, Throwable throwable);
    void stepCompleted(WorkflowResolver workflowResolver);
}
