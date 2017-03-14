package org.radrso.workflow.wfservice.executor;

import lombok.extern.log4j.Log4j;
import org.radrso.plugins.FileUtils;
import org.radrso.plugins.requests.entity.exceptions.ResponseCode;
import org.radrso.workflow.ConfigConstant;
import org.radrso.workflow.entities.config.WorkflowConfig;
import org.radrso.workflow.entities.config.items.Step;
import org.radrso.workflow.entities.exceptions.WFRuntimeException;
import org.radrso.workflow.entities.response.WFResponse;
import org.radrso.workflow.entities.wf.WorkflowErrorLog;
import org.radrso.workflow.entities.wf.WorkflowInstance;
import org.radrso.workflow.persistence.BaseWorkflowSynchronize;
import org.radrso.workflow.rmi.WorkflowFilesSync;
import org.radrso.workflow.rmi.WorkflowExecutor;
import org.radrso.workflow.wfservice.service.WorkflowExecuteStatusService;
import org.radrso.workflow.wfservice.service.WorkflowInstanceService;
import org.radrso.workflow.wfservice.service.WorkflowLogService;
import org.radrso.workflow.wfservice.service.WorkflowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;

/**
 * Created by rao-mengnan on 2017/3/14.
 */
@Component
@Log4j
public class WorkflowSyncironze implements BaseWorkflowSynchronize{
    public static final String ROOT = ConfigConstant.SERVICE_JAR_HOME;

    @Autowired
    protected WorkflowExecutor workflowExecutor;
    @Autowired
    protected WorkflowFilesSync workflowFilesSync;

    @Autowired
    protected WorkflowService workflowService;
    @Autowired
    protected WorkflowExecuteStatusService workflowExecuteStatusService;
    @Autowired
    protected WorkflowInstanceService workflowInstanceService;
    @Autowired
    protected WorkflowLogService workflowLogService;

    /**
     * 将jar导入到默认的jar文件夹目录下
     * @param workflowId 根据workflowId找到workflow配置，并根以此定义jar的目录
     * @return
     */
    @Override
    public boolean importJars(String workflowId) {
        WorkflowConfig workflowConfig = workflowService.getByWorkflowId(workflowId);
        String app = workflowConfig.getApplication();
        String jarsRoot = ROOT + app + File.separator ;
        List<String> jars = workflowConfig.getJars();
        if(jars == null)
            return false;

        jars.forEach(j->{
            File jarFile = new File(jarsRoot + j);
            if(!jarFile.exists())
                throw new WFRuntimeException(WFRuntimeException.JAR_FILE_NO_FOUND + String.format("[%s]", jarsRoot + j));

            WFResponse response = workflowFilesSync.checkAndImportJar(app, j);
            if(response.getCode() == ResponseCode.JAR_FILE_NOT_FOUND.code()) {
                log.info(String.format("UPLOAD Local JAR[%s]", app + "/" + j));
                response = workflowFilesSync.importJar(app, j, FileUtils.getByte(jarFile));
            }else
                log.info(String.format("NEEDN'T UPLOAD FILE[%s]", app + "/" + j));

            if(response.getCode() / 100 != 2)
                throw new WFRuntimeException("Jar file upload failed:" + response.getMsg());
        });
        return true;
    }

    @Override
    public boolean logError(WorkflowErrorLog log) {
        return workflowLogService.save(log);
    }

    @Override
    public boolean updateInstance(WorkflowInstance instance) {
        return workflowInstanceService.save(instance);
    }

    @Override
    public WorkflowConfig getWorkflow(String instanceId) {
        WorkflowInstance instance = workflowInstanceService.getByInstanceId(instanceId);
        return workflowService.getByWorkflowId(instance.getWorkflowId());
    }

    /**
     *
     * @param workflowId 工作流(配置)的ID
     * @return CREATED = "created";START = "started";STOP = "stopped";EXCEPTION = "exception";
     */
    @Override
    public String getWorkflowStatus(String workflowId) {
        WorkflowConfig workflowConfig = workflowService.getByWorkflowId(workflowId);
        if(workflowConfig == null)
            return null;
        workflowService.updateServiceStatus(workflowConfig);
        return workflowExecuteStatusService.getStatus(workflowId);
    }

    @Override
    public WFResponse startStep(Step step, Object[] params, String[] paramNames) {
        return workflowExecutor.execute(step, params, paramNames);
    }
}
