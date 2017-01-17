package org.radrso.workflow.wfservice.service.impl;

import org.radrso.workflow.entities.config.WorkflowConfig;
import org.radrso.workflow.wfservice.service.WorkflowService;
import org.springframework.stereotype.Service;

/**
 * Created by raomengnan on 17-1-17.
 */
@Service
public class WorkflowServiceImpl implements WorkflowService {
    @Override
    public boolean save(WorkflowConfig workflowConfig) {
        return false;
    }

    @Override
    public WorkflowConfig get(String workflowId) {
        return null;
    }
}
