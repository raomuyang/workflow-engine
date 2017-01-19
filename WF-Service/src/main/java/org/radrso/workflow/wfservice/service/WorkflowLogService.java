package org.radrso.workflow.wfservice.service;

import org.radrso.workflow.entities.wf.WorkflowErrorLog;

import java.util.List;

/**
 * Created by raomengnan on 17-1-19.
 */
public interface WorkflowLogService {
    boolean save(WorkflowErrorLog log);
    List<WorkflowErrorLog> getByWorkflowId(String workflowId);
    List<WorkflowErrorLog> getByInstanceId(String instanceId);
    boolean deleteByWorkflowId(String workflowId);
    boolean deleteByInstanceId(String instanceId);
}
