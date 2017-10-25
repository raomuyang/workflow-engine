package org.radrso.workflow.wfservice.repositories;

import org.radrso.workflow.entity.model.WorkflowErrorLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * Created by raomengnan on 17-1-19.
 */
public interface WorkflowErrorLogRepository extends MongoRepository<WorkflowErrorLog, String>{
    List<WorkflowErrorLog> findByWorkflowId(String wfId);
    Page<WorkflowErrorLog> findByWorkflowId(String wfId, Pageable pageable);
    List<WorkflowErrorLog> findByInstanceId(String instanceId);
    Page<WorkflowErrorLog> findByInstanceId(String instanceId, Pageable pageable);
    void deleteByWorkflowId(String wfId);
    void deleteByInstanceId(String instanceId);
}
