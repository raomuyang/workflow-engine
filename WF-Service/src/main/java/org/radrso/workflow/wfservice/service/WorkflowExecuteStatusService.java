package org.radrso.workflow.wfservice.service;

import org.radrso.workflow.entities.wf.WorkflowExecuteStatus;

/**
 * Created by raomengnan on 17-1-16.
 */
public interface WorkflowExecuteStatusService {
    public WorkflowExecuteStatus get(String workflowId);
    public boolean save(WorkflowExecuteStatus status);
    public String getStatus(String workflowId);
}
