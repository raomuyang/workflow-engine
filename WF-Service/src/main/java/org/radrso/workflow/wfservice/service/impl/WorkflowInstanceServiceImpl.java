package org.radrso.workflow.wfservice.service.impl;

import org.radrso.workflow.entities.config.items.Step;
import org.radrso.workflow.entities.wf.WorkflowInstance;
import org.radrso.workflow.wfservice.service.WorkflowInstanceService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by raomengnan on 17-1-17.
 */
@Service
public class WorkflowInstanceServiceImpl implements WorkflowInstanceService{
    @Override
    public WorkflowInstance newInstance(String workflowId) {
        return null;
    }

    @Override
    public boolean save(WorkflowInstance workflowInstance) {
        return false;
    }

    @Override
    public WorkflowInstance get(String workflowId, String wfInstanceId) {
        return null;
    }

    @Override
    public Map<String, String> currentProcess(String instanceId) {
        return null;
    }

    @Override
    public List<Step> finishedStep(String instanceId) {
        return null;
    }
}
