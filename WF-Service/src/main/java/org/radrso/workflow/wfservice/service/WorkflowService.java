package org.radrso.workflow.wfservice.service;

import org.radrso.workflow.entities.config.WorkflowConfig;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Created by raomengnan on 17-1-16.
 */
public interface WorkflowService {

    boolean save(WorkflowConfig workflowConfig);

    List<WorkflowConfig> getAll();

    WorkflowConfig getByWorkflowId(String workflowId);
    List<WorkflowConfig> getByApplication(String application);
    boolean delete(String workflowId);
    boolean deleteByApplication(String application);

    boolean transferJarFile(String application, MultipartFile originFile);
    void updateServiceStatus(WorkflowConfig workflowConfig);

}
