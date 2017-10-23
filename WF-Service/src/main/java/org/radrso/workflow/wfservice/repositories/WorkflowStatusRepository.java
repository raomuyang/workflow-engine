package org.radrso.workflow.wfservice.repositories;

import org.radrso.workflow.entities.model.WorkflowExecuteStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * Created by raomengnan on 17-1-18.
 */
public interface WorkflowStatusRepository extends MongoRepository<WorkflowExecuteStatus, String> {
    List<WorkflowExecuteStatus> findByApplication(String application);
    void deleteByApplication(String application);
}
