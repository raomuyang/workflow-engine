package org.radrso.workflow.wfservice.service;

import org.radrso.workflow.entities.wf.WorkflowExecuteStatus;

/**
 * Created by raomengnan on 17-1-16.
 */
public interface WorkflowExecuteStatusService {
    public WorkflowExecuteStatus get(String applicaton, String workflowId);
    public boolean save(WorkflowExecuteStatus status);
    public String getStatus(String application, String workflowId);
    public boolean deleteStatus(String application, String workflowId);
    public boolean deleteStatus(String application);
    public boolean clearAll();
}
