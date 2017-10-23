package org.radrso.workflow.wfservice.service;

import org.radrso.workflow.entities.model.WorkflowExecuteStatus;
import org.springframework.data.domain.Page;

/**
 * Created by raomengnan on 17-1-16.
 */
public interface WorkflowExecuteStatusService {

    WorkflowExecuteStatus get(String workflowId);

    Page<WorkflowExecuteStatus> getAll(int pno, int psize);

    public boolean save(WorkflowExecuteStatus status);

    String getStatus(String workflowId);

    public boolean deleteStatus(String workflowId);

    boolean deleteStatusByApplication(String application);

    public boolean clearAll();
}
