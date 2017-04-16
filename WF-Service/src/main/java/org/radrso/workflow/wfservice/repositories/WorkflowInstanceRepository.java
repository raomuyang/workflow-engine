package org.radrso.workflow.wfservice.repositories;

import org.radrso.workflow.entities.wf.WorkflowInstance;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * Created by raomengnan on 17-1-18.
 */
public interface WorkflowInstanceRepository extends MongoRepository<WorkflowInstance, String>{
    WorkflowInstance findByInstanceId(String instanceId);
    List<WorkflowInstance> findByWorkflowId(String workflowId);
    void deleteByWorkflowId(String workflowId);
}
