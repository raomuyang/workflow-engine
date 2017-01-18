package org.radrso.workflow.wfservice.service;

import org.radrso.workflow.entities.config.WorkflowConfig;

import java.util.List;

/**
 * Created by raomengnan on 17-1-16.
 */
public interface WorkflowService {

    public boolean save(WorkflowConfig workflowConfig);
    public WorkflowConfig getByWorkflowId(String workflowId);
    public List<WorkflowConfig> getByApplication(String application);
    public boolean delete(String workflowId);
    public boolean deleteByApplication(String application);
}
