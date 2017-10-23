package org.radrso.workflow.wfservice.service.impl;

import lombok.extern.log4j.Log4j;
import org.radrso.workflow.entities.model.WorkflowExecuteStatus;
import org.radrso.workflow.wfservice.repositories.WorkflowStatusRepository;
import org.radrso.workflow.wfservice.service.WorkflowExecuteStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

/**
 * Created by raomengnan on 17-1-17.
 */
@Service
@Log4j
public class WorkflowExecuteStatusServiceImpl implements WorkflowExecuteStatusService{
    @Autowired
    private WorkflowStatusRepository workflowStatusRepository;

    @Override
    public WorkflowExecuteStatus get(String workflowId) {
        return workflowStatusRepository.findOne(workflowId);
    }

    @Override
    public Page<WorkflowExecuteStatus> getAll(int pno, int psize){
        PageRequest pageRequest = new PageRequest(pno, psize);
        return workflowStatusRepository.findAll(pageRequest);
    }
    @Override
    public boolean save(WorkflowExecuteStatus status) {
        if(status == null || status.getWorkflowId() == null)
            return false;
        workflowStatusRepository.save(status);
        return true;
    }

    @Override
    public String getStatus(String workflowId) {
        WorkflowExecuteStatus status = get(workflowId);
        if(status != null)
            return status.getStatus();
        return null;
    }

    @Override
    public boolean deleteStatus(String workflowId) {
        if(workflowId == null)
            return false;
        workflowStatusRepository.delete(workflowId);
        return true;
    }


    @Override
    public boolean deleteStatusByApplication(String application) {
        if (application == null)
            return false;
        workflowStatusRepository.deleteByApplication(application);
        return true;
    }

    @Override
    public boolean clearAll() {
        log.info("clear all");
        workflowStatusRepository.deleteAll();
        return true;
    }
}
