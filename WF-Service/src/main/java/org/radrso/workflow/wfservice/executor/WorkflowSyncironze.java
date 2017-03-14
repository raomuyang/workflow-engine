package org.radrso.workflow.wfservice.executor;

import org.radrso.workflow.ConfigConstant;
import org.radrso.workflow.entities.config.WorkflowConfig;
import org.radrso.workflow.entities.config.items.Step;
import org.radrso.workflow.entities.response.WFResponse;
import org.radrso.workflow.entities.wf.WorkflowErrorLog;
import org.radrso.workflow.entities.wf.WorkflowInstance;
import org.radrso.workflow.exec.StepCommander;
import org.radrso.workflow.persistence.BaseWorkflowSynchronize;
import org.radrso.workflow.rmi.WorkflowCommander;
import org.radrso.workflow.rmi.WorkflowInstanceExecutor;
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
public class WorkflowSyncironze implements BaseWorkflowSynchronize{
    public static final String ROOT = ConfigConstant.SERVICE_JAR_HOME;

    @Autowired
    protected WorkflowInstanceExecutor workflowInstanceExecutor;
    @Autowired
    protected WorkflowCommander workflowCommander;

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

        return StepCommander.importJars(jars, jarsRoot);
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
        return workflowInstanceExecutor.execute(step, params, paramNames);
    }
}
