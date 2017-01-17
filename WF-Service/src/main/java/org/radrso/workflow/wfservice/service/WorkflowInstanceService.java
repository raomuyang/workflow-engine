package org.radrso.workflow.wfservice.service;

import org.radrso.workflow.entities.config.items.Step;
import org.radrso.workflow.entities.wf.WorkflowInstance;

import java.util.List;
import java.util.Map;

/**
 * Created by raomengnan on 17-1-16.
 */
public interface WorkflowInstanceService {
    public WorkflowInstance newInstance(String workflowId);
    public boolean save(WorkflowInstance workflowInstance);
    public WorkflowInstance get(String workflowId, String wfInstanceId);
    public Map<String, String> currentProcess(String instanceId);
    public List<Step> finishedStep(String instanceId);
}
