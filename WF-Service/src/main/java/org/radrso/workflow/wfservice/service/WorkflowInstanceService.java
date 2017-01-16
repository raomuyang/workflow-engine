package org.radrso.workflow.wfservice.service;

import org.radrso.workflow.entities.wf.WorkflowInstance;

/**
 * Created by raomengnan on 17-1-16.
 */
public interface WorkflowInstanceService {
    public boolean save(WorkflowInstance workflowInstance);
    public WorkflowInstance get(String workflowId, String wfInstanceId);
}
