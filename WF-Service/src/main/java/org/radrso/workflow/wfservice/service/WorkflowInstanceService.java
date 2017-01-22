package org.radrso.workflow.wfservice.service;

import org.radrso.workflow.entities.wf.WorkflowInstance;

import java.util.List;
import java.util.Map;

/**
 * Created by raomengnan on 17-1-16.
 */
public interface WorkflowInstanceService {
    public WorkflowInstance newInstance(String workflowId);
    public boolean save(WorkflowInstance workflowInstance);
    public WorkflowInstance getByInstanceId(String instanceId);

    List<WorkflowInstance> getInstanceDetails(String instanceId);

    public List<WorkflowInstance> getByWorkflowId(String workflowId);

    int count(String workflowId);

    int countFinished(String workflowId);

    public Map<String, String> currentProcess(String instanceId);
    public Map<String, List<String>> finishedStep(String instanceId);
    public boolean delete(String instanceId);
    public boolean deleteByWorkflowId(String workflowId);
}
