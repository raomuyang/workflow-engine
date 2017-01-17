package org.radrso.workflow.wfservice.service.impl;

import org.radrso.workflow.entities.wf.WorkflowExecuteStatus;
import org.radrso.workflow.wfservice.service.WorkflowExecuteStatusService;
import org.springframework.stereotype.Service;

/**
 * Created by raomengnan on 17-1-17.
 */
@Service
public class WorkflowExecuteStatusServiceImpl implements WorkflowExecuteStatusService{
    @Override
    public WorkflowExecuteStatus get(String workflowId) {
        return null;
    }

    @Override
    public boolean save(WorkflowExecuteStatus status) {
        return false;
    }

    @Override
    public String getStatus(String workflowId) {
        return null;
    }
}
