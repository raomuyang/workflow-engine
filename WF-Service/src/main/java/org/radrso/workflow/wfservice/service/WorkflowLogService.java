package org.radrso.workflow.wfservice.service;

import org.radrso.workflow.entity.model.WorkflowErrorLog;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Created by raomengnan on 17-1-19.
 */
public interface WorkflowLogService {
    boolean save(WorkflowErrorLog log);
    List<WorkflowErrorLog> getByWorkflowId(String workflowId);

    Page<WorkflowErrorLog> getByWorkflowId(String workflowId, int pno, int psize);

    List<WorkflowErrorLog> getByInstanceId(String instanceId);

    Page<WorkflowErrorLog> getByInstanceId(String instanceId, int pno, int psize);

    boolean deleteByWorkflowId(String workflowId);
    boolean deleteByInstanceId(String instanceId);
    int count(String workflowId);
}
