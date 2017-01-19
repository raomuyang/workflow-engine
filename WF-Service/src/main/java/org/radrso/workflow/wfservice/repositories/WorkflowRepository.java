package org.radrso.workflow.wfservice.repositories;

import org.radrso.workflow.entities.config.WorkflowConfig;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * Created by raomengnan on 17-1-18.
 */
public interface WorkflowRepository extends MongoRepository<WorkflowConfig, String>{
    public List<WorkflowConfig> findByApplication(String application);
    public List<WorkflowConfig> findByOwner(String owner);
    public void deleteByApplication(String application);
}
