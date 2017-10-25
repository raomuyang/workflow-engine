package org.radrso.workflow.wfservice.executor;

import lombok.extern.log4j.Log4j;
import org.radrso.plugins.FileUtils;
import org.radrso.plugins.requests.entity.ResponseCode;
import org.radrso.workflow.base.Operations;
import org.radrso.workflow.constant.EngineConstant;
import org.radrso.workflow.constant.ExceptionCode;
import org.radrso.workflow.entities.model.JarFile;
import org.radrso.workflow.entities.schema.WorkflowSchema;
import org.radrso.workflow.entities.schema.items.Step;
import org.radrso.workflow.entities.exceptions.WFRuntimeException;
import org.radrso.workflow.entities.model.WorkflowResult;
import org.radrso.workflow.entities.model.WorkflowErrorLog;
import org.radrso.workflow.entities.model.WorkflowInstance;
import org.radrso.workflow.base.Commander;
import org.radrso.workflow.wfservice.service.WorkflowExecuteStatusService;
import org.radrso.workflow.wfservice.service.WorkflowInstanceService;
import org.radrso.workflow.wfservice.service.WorkflowLogService;
import org.radrso.workflow.wfservice.service.WorkflowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rao-mengnan on 2017/3/14.
 */
@Component
@Log4j
public class CommanderImpl implements Commander {
    public static final String ROOT = EngineConstant.SERVICE_JAR_HOME;

    @Autowired
    protected Operations operations;

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
    public boolean jarFilesSync(String workflowId) {
        WorkflowSchema workflowConfig = workflowService.getByWorkflowId(workflowId);
        String app = workflowConfig.getApplication();
        String jarsRoot = ROOT + app + File.separator;
        List<String> jars = workflowConfig.getJars();
        if(jars == null)
            return false;

        List<JarFile> jarFileEntities = workflowService.listApplicationJarFiles(app);
        List<String> remoteFiles = new ArrayList<>();
        if (jarFileEntities != null) {
            jarFileEntities.forEach(jFiles -> {
                remoteFiles.add(jFiles.getName());
            });
        }
        jars.forEach(name->{
            File jarFile = new File(jarsRoot + name);
            if(!jarFile.exists()) {
                String msg = WFRuntimeException.JAR_FILE_NO_FOUND + String.format("[%s]", jarsRoot + name);
                throw new WFRuntimeException(msg, ExceptionCode.JAR_FILE_NOT_FOUND.code());
            }

            // 若数据库中不存在
            if (!remoteFiles.contains(name)) {
                log.warn(String.format("Remote didn't exists [%s | %s]", app, name));
                try {
                    workflowService.saveJarFile(app, name, FileUtils.getByte(jarFile));
                } catch (IOException e) {
                    log.error(e);
                    throw new WFRuntimeException("Jar file save failed: " + e.getMessage(), ResponseCode.UNKNOW.code());
                }
            }

            WorkflowResult response = operations.checkAndImportJar(app, name);
            if(response.getCode() == ExceptionCode.JAR_FILE_NOT_FOUND.code()) {
                log.info(String.format("UPLOAD Local JAR[%s]", app + "/" + name));
                response = operations.checkAndImportJar(app, name);
            } else
                log.info(String.format("NEEDN'T UPLOAD FILE[%s]", app + "/" + name));

            if(response.getCode() / 100 != 2) {
                throw new WFRuntimeException("Jar file upload failed:" + response.getMsg(), ResponseCode.SOCKET_EXCEPTION.code());
            }
        });
        return true;
    }

    @Override
    public boolean saveErrorLog(WorkflowErrorLog log) {
        return workflowLogService.save(log);
    }

    @Override
    public boolean updateInstance(WorkflowInstance instance) {
        return workflowInstanceService.save(instance);
    }

    @Override
    public WorkflowSchema getWorkflowConfig(String instanceId) {
        String id = instanceId;
        if (id.contains("-")) {
            id = id.substring(0, id.indexOf("-"));
        }
        WorkflowInstance instance = workflowInstanceService.getByInstanceId(id);
        if (instance == null) {
            return null;
        }
        return workflowService.getByWorkflowId(instance.getWorkflowId());
    }

    /**
     *
     * @param workflowId 工作流(配置)的ID
     * @return CREATED = "created";START = "started";STOP = "stopped";EXCEPTION = "exception";
     */
    @Override
    public String getWorkflowStatus(String workflowId) {
        WorkflowSchema workflowConfig = workflowService.getByWorkflowId(workflowId);
        if(workflowConfig == null)
            return null;
        workflowService.updateServiceStatus(workflowConfig);
        return workflowExecuteStatusService.getStatus(workflowId);
    }

    @Override
    public WorkflowResult runStepAction(Step step, Object[] params, String[] paramNames) {
        return operations.executeStepAction(step, params, paramNames);
    }

    @Override
    public WorkflowInstance getInstance(String instanceId) {
        return workflowInstanceService.getByInstanceId(instanceId);
    }
}
