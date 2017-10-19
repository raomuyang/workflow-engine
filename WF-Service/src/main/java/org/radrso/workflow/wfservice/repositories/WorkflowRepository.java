package org.radrso.workflow.wfservice.repositories;

import org.radrso.workflow.entities.schema.WorkflowSchema;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * Created by raomengnan on 17-1-18.
 */
public interface WorkflowRepository extends MongoRepository<WorkflowSchema, String>{
    List<WorkflowSchema> findByApplication(String application);
    List<WorkflowSchema> findByOwner(String owner);
    void deleteByApplication(String application);
}
