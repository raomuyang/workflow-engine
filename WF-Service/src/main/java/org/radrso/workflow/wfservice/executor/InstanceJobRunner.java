package org.radrso.workflow.wfservice.executor;

import org.radrso.workflow.resolvers.FlowResolver;
import org.radrso.workflow.entities.response.WFResponse;

/**
 * Created by raomengnan on 17-1-14.
 */
public interface InstanceJobRunner {

    WFResponse startExecute(FlowResolver workflowResolver, boolean rerun);

    boolean interrupt(String instanceId);
}
