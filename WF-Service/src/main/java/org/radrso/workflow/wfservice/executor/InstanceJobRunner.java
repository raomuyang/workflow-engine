package org.radrso.workflow.wfservice.executor;

import org.radrso.workflow.resolvers.WorkflowResolver;
import org.radrso.workflow.entities.info.WorkflowResult;

/**
 * Created by raomengnan on 17-1-14.
 */
public interface InstanceJobRunner {

    WorkflowResult startExecute(WorkflowResolver workflowResolver, boolean rerun);

    boolean interrupt(String instanceId);
}
