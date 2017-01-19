package org.radrso.workflow.wfservice.service.exec;

import lombok.Data;
import org.radrso.plugins.FileUtils;
import org.radrso.workflow.entities.config.WorkflowConfig;
import org.radrso.workflow.entities.response.WFResponse;
import org.radrso.workflow.rmi.WorkflowInstanceExecutor;
import org.radrso.workflow.wfservice.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

/**
 * Created by raomengnan on 17-1-19.
 */
@Service
public class WorkflowCommandServiceImpl implements WorkflowCommandService{
    @Autowired
    protected WorkflowInstanceExecutor workflowInstanceExecutor;

    @Autowired
    protected WorkflowService workflowService;
    @Autowired
    protected WorkflowExecuteStatusService workflowExecuteStatusService;
    @Autowired
    protected WorkflowInstanceService workflowInstanceService;
    @Autowired
    protected WorkflowLogService workflowLogService;

    @Override
    public WorkflowInstanceExecutor getInstanceExecutor() {
        return this.workflowInstanceExecutor;
    }

    @Override
    public WorkflowService getWFService() {
        return this.workflowService;
    }

    @Override
    public WorkflowExecuteStatusService getStatusService() {
        return this.workflowExecuteStatusService;
    }

    @Override
    public WorkflowInstanceService getInstanceService() {
        return this.workflowInstanceService;
    }

    @Override
    public WorkflowLogService getLogService() {
        return this.workflowLogService;
    }

    public boolean importJars(String workflowId) {
        WorkflowConfig workflowConfig = workflowService.getByWorkflowId(workflowId);
        String app = workflowConfig.getApplication();
        String root = FileUtils.getProjectHome() + File.separator + app + File.separator ;
        List<String> jars = workflowConfig.getJars();
        if(jars == null)
            return false;

        jars.forEach(j->{});

        return false;
    }
}
