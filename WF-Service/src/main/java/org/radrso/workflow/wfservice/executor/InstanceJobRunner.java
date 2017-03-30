package org.radrso.workflow.wfservice.executor;

import org.radrso.workflow.resolvers.BaseWorkflowConfigResolver;
import org.radrso.workflow.entities.response.WFResponse;

/**
 * Created by raomengnan on 17-1-14.
 */
public interface InstanceJobRunner {

    WFResponse startExecute(BaseWorkflowConfigResolver workflowResolver, boolean rerun);

    boolean interrupt(String instanceId);
}
