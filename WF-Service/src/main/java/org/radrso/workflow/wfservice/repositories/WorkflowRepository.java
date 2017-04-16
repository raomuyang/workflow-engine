package org.radrso.workflow.wfservice.repositories;

import org.radrso.workflow.entities.config.WorkflowConfig;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * Created by raomengnan on 17-1-18.
 */
public interface WorkflowRepository extends MongoRepository<WorkflowConfig, String>{
    List<WorkflowConfig> findByApplication(String application);
    List<WorkflowConfig> findByOwner(String owner);
    void deleteByApplication(String application);
}
