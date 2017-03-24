package org.radrso.workflow.exec.base;

import org.radrso.workflow.resolvers.BaseWorkflowConfigResolver;


/**
 * Created by raomengnan on 17-1-17.
 */

public interface BaseStepAction {
    void stepNext(BaseWorkflowConfigResolver workflowResolver);
    void stepError(BaseWorkflowConfigResolver workflowResolver, Throwable throwable);
    void stepCompleted(BaseWorkflowConfigResolver workflowResolver);
}
