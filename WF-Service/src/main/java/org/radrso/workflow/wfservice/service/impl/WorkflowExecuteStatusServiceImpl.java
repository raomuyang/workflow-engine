package org.radrso.workflow.wfservice.service.impl;

import lombok.extern.log4j.Log4j;
import org.radrso.workflow.entities.wf.WorkflowExecuteStatus;
import org.radrso.workflow.wfservice.repositories.WorkflowStatusRepository;
import org.radrso.workflow.wfservice.service.WorkflowExecuteStatusService;
import org.springframework.beans.factory.annotation.Autowired;
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
    public WorkflowExecuteStatus get(String applicaton, String workflowId) {
        return workflowStatusRepository.findByApplicationAndWorkflowId(applicaton, workflowId);
    }

    @Override
    public boolean save(WorkflowExecuteStatus status) {
        if(status == null)
            return false;
        workflowStatusRepository.save(status);
        return true;
    }

    @Override
    public String getStatus(String application, String workflowId) {
        WorkflowExecuteStatus status = get(application, workflowId);
        if(status != null)
            return status.getStatus();
        return null;
    }

    @Override
    public boolean deleteStatus(String application, String workflowId) {
        if(application == null || workflowId == null)
            return false;
        workflowStatusRepository.deleteByApplicationAndWorkflowId(application, workflowId);
        return true;
    }


    @Override
    public boolean deleteStatus(String application) {
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
