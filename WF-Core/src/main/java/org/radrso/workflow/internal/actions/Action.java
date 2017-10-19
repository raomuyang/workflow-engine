package org.radrso.workflow.internal.actions;

import org.radrso.workflow.resolvers.WorkflowResolver;

/**
 * Created by rao-mengnan on 2017/5/18.
 */
public interface Action {
    Action setResolver(WorkflowResolver resolver);
}
