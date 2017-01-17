package org.radrso.workflow.wfservice.service;

import org.radrso.workflow.resolvers.WorkflowResolver;
import org.radrso.workflow.entities.response.WFResponse;

/**
 * Created by raomengnan on 17-1-14.
 */
public interface WorkflowExecuteService {

    WFResponse execute(WorkflowResolver workflowResolver);


}
