package org.radrso.workflow.wfservice.subscribe;

import org.radrso.workflow.resolvers.WorkflowResolver;


/**
 * Created by raomengnan on 17-1-17.
 */

public interface StepAction {
    void stepNext(WorkflowResolver workflowResolver);
    void stepError(WorkflowResolver workflowResolver, Throwable throwable);
    void stepCompleted(WorkflowResolver workflowResolver);
}
