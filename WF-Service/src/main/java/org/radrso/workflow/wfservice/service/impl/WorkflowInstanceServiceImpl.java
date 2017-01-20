package org.radrso.workflow.wfservice.service.impl;

import org.bson.types.ObjectId;
import org.radrso.workflow.entities.config.WorkflowConfig;
import org.radrso.workflow.entities.config.items.Step;
import org.radrso.workflow.entities.wf.WorkflowInstance;
import org.radrso.workflow.wfservice.repositories.WorkflowInstanceRepository;
import org.radrso.workflow.wfservice.repositories.WorkflowRepository;
import org.radrso.workflow.wfservice.service.WorkflowInstanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by raomengnan on 17-1-17.
 */
@Service
public class WorkflowInstanceServiceImpl implements WorkflowInstanceService{
    @Autowired
    private WorkflowInstanceRepository workflowInstanceRepository;
    @Autowired
    private WorkflowRepository workflowRepository;

    @Override
    public WorkflowInstance newInstance(String workflowId) {
        WorkflowConfig workflowConfig = workflowRepository.findOne(workflowId);
        if(workflowConfig == null)
            return null;

        ObjectId id = new ObjectId();
        WorkflowInstance workflowInstance = new WorkflowInstance(workflowId, id.toHexString());
        save(workflowInstance);
        return workflowInstance;
    }

    @Override
    public boolean save(WorkflowInstance workflowInstance) {
        if(workflowInstance == null)
            return false;
        workflowInstanceRepository.save(workflowInstance);
        return true;
    }

    @Override
    public WorkflowInstance getByInstanceId(String instanceId) {
        if(instanceId == null)
            return null;
        return workflowInstanceRepository.findOne(instanceId);
    }

    @Override
    public List<WorkflowInstance> getByWorkflowId(String workflowId) {
        if(workflowId == null)
            return new ArrayList<>();
        return workflowInstanceRepository.findByWorkflowId(workflowId);
    }


    @Override
    public Map<String, String> currentProcess(String instanceId) {
        WorkflowInstance wi = getByInstanceId(instanceId);
        if(wi != null)
            return wi.getStepProcess();
        return null;
    }

    @Override
    public List<Step> finishedStep(String instanceId) {
        return null;
    }

    @Override
    public boolean delete(String instanceId) {
        if(instanceId == null)
            return false;
        workflowInstanceRepository.delete(instanceId);
        return true;
    }

    @Override
    public boolean deleteByWorkflowId(String workflowId) {
        if(workflowId == null)
            return false;
        workflowInstanceRepository.deleteByWorkflowId(workflowId);
        return true;
    }
}
