package org.radrso.workflow.wfservice.service;

import org.radrso.workflow.entities.config.WorkflowConfig;

/**
 * Created by raomengnan on 17-1-16.
 */
public interface WorkflowService {

    public boolean save(WorkflowConfig workflowConfig);
    public WorkflowConfig get(String workflowId);
}
