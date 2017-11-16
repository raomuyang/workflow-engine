package org.radrso.workflow.wfservice.service;

import org.radrso.workflow.entity.model.WorkflowInstance;

import java.util.List;
import java.util.Map;

/**
 * Created by raomengnan on 17-1-16.
 */
public interface WorkflowInstanceService {
    WorkflowInstance newInstance(String workflowId);
    boolean save(WorkflowInstance workflowInstance);
    WorkflowInstance getByInstanceId(String instanceId);

    List<WorkflowInstance> getInstanceAllBranchesDetail(String instanceId);

    List<WorkflowInstance> getByWorkflowId(String workflowId);

    int count(String workflowId);

    int countFinished(String workflowId);

    Map<String, Map> currentProcess(String instanceId);
    Map<String, List<String>> finishedStep(String instanceId);
    boolean delete(String instanceId);
    boolean deleteByWorkflowId(String workflowId);
}
