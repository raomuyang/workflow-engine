package org.radrso.workflow.wfservice.service.impl;

import org.radrso.workflow.entities.model.WorkflowErrorLog;
import org.radrso.workflow.wfservice.repositories.WorkflowErrorLogRepository;
import org.radrso.workflow.wfservice.service.WorkflowLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by raomengnan on 17-1-19.
 */
@Service
public class WorkflowLogServiceImpl implements WorkflowLogService {
    @Autowired
    private WorkflowErrorLogRepository workflowErrorLogRepository;
    @Override
    public boolean save(WorkflowErrorLog log) {
        if(log == null)
            return false;
        workflowErrorLogRepository.save(log);
        return true;
    }

    @Override
    public List<WorkflowErrorLog> getByWorkflowId(String workflowId) {
        if(workflowId == null)
            return null;
        return workflowErrorLogRepository.findByWorkflowId(workflowId);
    }

    @Override
    public Page<WorkflowErrorLog> getByWorkflowId(String workflowId, int pno, int psize){
        if(workflowId == null)
            return null;
        return workflowErrorLogRepository.findByWorkflowId(workflowId, new PageRequest(pno, psize));
    }

    @Override
    public List<WorkflowErrorLog> getByInstanceId(String instanceId) {
        if(instanceId == null)
            return null;
        return workflowErrorLogRepository.findByInstanceId(instanceId);
    }

    @Override
    public Page<WorkflowErrorLog> getByInstanceId(String instanceId, int pno, int psize){
        if(instanceId == null)
            return null;
        return workflowErrorLogRepository.findByInstanceId(instanceId, new PageRequest(pno, psize));
    }

    @Override
    public boolean deleteByWorkflowId(String workflowId) {
        if(workflowId == null)
            return false;
        workflowErrorLogRepository.deleteByWorkflowId(workflowId);
        return true;
    }

    @Override
    public boolean deleteByInstanceId(String instanceId) {
        if(instanceId == null)
            return false;
        workflowErrorLogRepository.deleteByInstanceId(instanceId);
        return true;
    }

    @Override
    public int count(String workflowId) {
        List<WorkflowErrorLog> logs = getByWorkflowId(workflowId);
        return logs == null?0:logs.size();
    }
}
