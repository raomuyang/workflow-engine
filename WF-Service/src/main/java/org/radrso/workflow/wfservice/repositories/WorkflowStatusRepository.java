package org.radrso.workflow.wfservice.repositories;

import org.radrso.workflow.entity.model.WorkflowRuntimeState;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * Created by raomengnan on 17-1-18.
 */
public interface WorkflowStatusRepository extends MongoRepository<WorkflowRuntimeState, String> {
    List<WorkflowRuntimeState> findByApplication(String application);
    void deleteByApplication(String application);
}
