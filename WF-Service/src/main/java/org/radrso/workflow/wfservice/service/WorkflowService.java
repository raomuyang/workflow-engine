package org.radrso.workflow.wfservice.service;

import org.radrso.workflow.entities.config.WorkflowConfig;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Created by raomengnan on 17-1-16.
 */
public interface WorkflowService {

    boolean save(WorkflowConfig workflowConfig);

    List<WorkflowConfig> getAll();

    Page<WorkflowConfig> getAll(int pno, int psize);

    WorkflowConfig getByWorkflowId(String workflowId);
    List<WorkflowConfig> getByApplication(String application);
    boolean delete(String workflowId);
    boolean deleteByApplication(String application);

    /**
     * 以workflowId将文件保存到相应的路径
     * @param workflowId
     * @param originFile
     * @return
     */
    boolean transferJarFile(String workflowId, MultipartFile originFile);
    void updateServiceStatus(WorkflowConfig workflowConfig);

}
