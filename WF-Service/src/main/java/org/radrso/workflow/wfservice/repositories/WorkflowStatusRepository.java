package org.radrso.workflow.wfservice.repositories;

import org.radrso.workflow.entities.wf.WorkflowExecuteStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by raomengnan on 17-1-18.
 */
public interface WorkflowStatusRepository extends MongoRepository<WorkflowExecuteStatus, String> {
    WorkflowExecuteStatus findByApplicationAndWorkflowId(String application, String workflow);
    void deleteByApplication(String application);
    void deleteByApplicationAndWorkflowId(String application, String workflowId);
}
