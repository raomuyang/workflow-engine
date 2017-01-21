package org.radrso.workflow.wfservice.service;

import org.radrso.workflow.entities.config.items.Step;
import org.radrso.workflow.entities.response.WFResponse;
import org.radrso.workflow.entities.wf.WorkflowErrorLog;
import org.radrso.workflow.entities.wf.WorkflowInstance;
import org.radrso.workflow.resolvers.WorkflowResolver;
import org.radrso.workflow.rmi.WorkflowInstanceExecutor;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by raomengnan on 17-1-19.
 */
public interface WorkflowCommandService {

    void importJars(String workflowId);

    boolean logError(WorkflowErrorLog log);

    boolean updateInstance(WorkflowInstance instance);

    WFResponse execute(Step step, Object[] params, String[] paramNames);

    String getWFStatus(String workflowId);

    WorkflowResolver branchInstance(String instanceId);
}
