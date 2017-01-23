package org.radrso.workflow.wfservice.service.impl;

import lombok.extern.log4j.Log4j;
import org.radrso.plugins.FileUtils;
import org.radrso.workflow.StandardString;
import org.radrso.workflow.entities.config.WorkflowConfig;
import org.radrso.workflow.entities.wf.WorkflowExecuteStatus;
import org.radrso.workflow.wfservice.repositories.WorkflowRepository;
import org.radrso.workflow.wfservice.repositories.WorkflowStatusRepository;
import org.radrso.workflow.wfservice.service.WorkflowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by raomengnan on 17-1-17.
 */
@Log4j
@Service
public class WorkflowServiceImpl implements WorkflowService {
    @Autowired
    private WorkflowRepository workflowRepository;
    @Autowired
    private WorkflowStatusRepository statusRepository;

    @Override
    public boolean save(WorkflowConfig workflowConfig) {
        if(workflowConfig == null)
            return false;
        workflowRepository.save(workflowConfig);
        return true;
    }

    @Override
    public List<WorkflowConfig> getAll(){
        return workflowRepository.findAll();
    }
    @Override
    public WorkflowConfig getByWorkflowId(String workflowId) {
        if(workflowId == null)
            return null;
        return workflowRepository.findOne(workflowId);
    }

    @Override
    public List<WorkflowConfig> getByApplication(String application) {
        if(application == null)
            return new ArrayList<>();
        return workflowRepository.findByApplication(application);
    }

    @Override
    public boolean delete(String workflowId) {
        log.info("Delete " + workflowId);
        if(workflowId == null)
            return false;
        workflowRepository.delete(workflowId);
        return true;
    }

    @Override
    public boolean deleteByApplication(String application) {
        if(application == null)
            return false;
        workflowRepository.deleteByApplication(application);
        return true;
    }

    @Override
    public boolean transferJarFile(String application, MultipartFile originFile) {
        String jarRoots = StandardString.SERVICE_JAR_HOME + application + File.separator;
        String originalFileName = originFile.getOriginalFilename();
        try {
           return FileUtils.writeFile(jarRoots , originalFileName, originFile.getBytes());
        } catch (IOException e) {
            log.error(e);
            return false;
        }
    }

    @Override
    public void updateServiceStatus(WorkflowConfig workflowConfig){
        if(workflowConfig == null)
            return;

        try {

            Date start = workflowConfig.getStartTime();
            Date stop = workflowConfig.getStopTime();

            WorkflowExecuteStatus status = statusRepository.findOne(workflowConfig.getId());
            if (status == null)
                status = new WorkflowExecuteStatus(workflowConfig.getId(), workflowConfig.getApplication(), WorkflowExecuteStatus.CREATED, null);

            Date current = new Date();
            if (current.after(start) && current.before(stop))
                status.setStatus(WorkflowExecuteStatus.START);
            else if (current.after(stop))
                status.setStatus(WorkflowExecuteStatus.STOP);

            statusRepository.save(status);
        }catch (Throwable e){
            log.error(e);
        }
    }
}
