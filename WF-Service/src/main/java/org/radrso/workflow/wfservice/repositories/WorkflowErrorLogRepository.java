package org.radrso.workflow.wfservice.repositories;

import org.radrso.workflow.entities.wf.WorkflowErrorLog;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * Created by raomengnan on 17-1-19.
 */
public interface WorkflowErrorLogRepository extends MongoRepository<WorkflowErrorLog, String>{
    public List<WorkflowErrorLog> findByWorkflowId(String wfId);
    public List<WorkflowErrorLog> findByInstanceId(String instanceId);
    public void deleteByWorkflowId(String wfId);
    public void deleteByInstanceId(String instanceId);
}
