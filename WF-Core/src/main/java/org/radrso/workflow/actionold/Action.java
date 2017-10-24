package org.radrso.workflow.actionold;

import org.radrso.workflow.resolvers.WorkflowResolver;

/**
 * Created by rao-mengnan on 2017/5/18.
 */
public interface Action {
    Action setResolver(WorkflowResolver resolver);
}
