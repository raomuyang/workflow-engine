package org.radrso.workflow.wfservice.service;

import org.radrso.workflow.entities.model.WorkflowRuntimeState;
import org.springframework.data.domain.Page;

/**
 * Created by raomengnan on 17-1-16.
 */
public interface WorkflowExecuteStatusService {

    WorkflowRuntimeState get(String workflowId);

    Page<WorkflowRuntimeState> getAll(int pno, int psize);

    public boolean save(WorkflowRuntimeState status);

    String getStatus(String workflowId);

    public boolean deleteStatus(String workflowId);

    boolean deleteStatusByApplication(String application);

    public boolean clearAll();
}
