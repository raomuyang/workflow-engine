package org.radrso.workflow.wfservice.service.impl;

import lombok.extern.log4j.Log4j;
import org.radrso.plugins.FileUtils;
import org.radrso.workflow.ConfigConstant;
import org.radrso.workflow.entities.config.WorkflowConfig;
import org.radrso.workflow.entities.wf.WorkflowExecuteStatus;
import org.radrso.workflow.wfservice.repositories.WorkflowRepository;
import org.radrso.workflow.wfservice.repositories.WorkflowStatusRepository;
import org.radrso.workflow.wfservice.service.WorkflowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
        if(workflowConfig == null || workflowConfig.getApplication() == null || workflowConfig.getId() == null)
            return false;
        workflowRepository.save(workflowConfig);
        return true;
    }

    @Override
    public List<WorkflowConfig> getAll(){
        return workflowRepository.findAll();
    }

    @Override
    public Page<WorkflowConfig> getAll(int pno, int psize){
        return workflowRepository.findAll(new PageRequest(pno, psize));
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
    public void stopWorkflow(String workflow) {
        WorkflowConfig workflowConfig = getByWorkflowId(workflow);
        if (workflowConfig == null) {
            return;
        }
        workflowConfig.setStopTime(new Date());
        workflowRepository.save(workflowConfig);

        WorkflowExecuteStatus executeStatus = statusRepository.findOne(workflow);
        String status = executeStatus.getStatus();
        if (status.equals(WorkflowExecuteStatus.STOP)){
            return;
        }
        executeStatus.setStatus(WorkflowExecuteStatus.STOP);
        statusRepository.save(executeStatus);
    }

    @Override
    public boolean restartWorkflow(String workflow, Date stopTime) {
        WorkflowConfig workflowConfig = getByWorkflowId(workflow);
        if (workflowConfig == null || workflowConfig.getStartTime().after(stopTime)) {
            return false;
        }
        workflowConfig.setStartTime(new Date());
        workflowConfig.setStopTime(stopTime);
        workflowRepository.save(workflowConfig);

        WorkflowExecuteStatus executeStatus = statusRepository.findOne(workflow);
        String status = executeStatus.getStatus();
        if (status.equals(WorkflowExecuteStatus.START)) {
            return true;
        }
        executeStatus.setStatus(WorkflowExecuteStatus.START);
        statusRepository.save(executeStatus);
        return true;
    }

    @Override
    public boolean transferJarFile(String workflowId, MultipartFile originFile) {
        if (!originFile.getOriginalFilename().endsWith(".jar")){
            return false;
        }

        WorkflowConfig workflowConfig = getByWorkflowId(workflowId);
        String jarRoots = ConfigConstant.SERVICE_JAR_HOME + workflowId + File.separator;
        String originalFileName = originFile.getOriginalFilename();
        try {
           boolean res = FileUtils.writeFile(jarRoots , originalFileName, originFile.getBytes());
           if (res){
               workflowConfig.getJars().add(originalFileName);
               save(workflowConfig);
           }
           return res;
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

            WorkflowExecuteStatus executeStatus = statusRepository.findOne(workflowConfig.getId());
            String status = executeStatus.getStatus();
            if (executeStatus == null)
                executeStatus = new WorkflowExecuteStatus(workflowConfig.getId(), workflowConfig.getApplication(), WorkflowExecuteStatus.CREATED, null);

            // 针对不同的状态进行修正
            Date current = new Date();
            if (current.after(start) && current.before(stop) && status.equals(WorkflowExecuteStatus.CREATED)) {
                executeStatus.setStatus(WorkflowExecuteStatus.START);
            }
            else if (current.after(stop) && status.equals(WorkflowExecuteStatus.START)) {
                executeStatus.setStatus(WorkflowExecuteStatus.STOP);
            }
            if (!executeStatus.getStatus().equals(status)) {
                statusRepository.save(executeStatus);
            }
        }catch (Throwable e){
            log.error(e);
        }
    }
}
