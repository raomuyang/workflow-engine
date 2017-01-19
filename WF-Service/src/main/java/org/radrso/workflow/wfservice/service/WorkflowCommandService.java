package org.radrso.workflow.wfservice.service;

import org.radrso.workflow.rmi.WorkflowInstanceExecutor;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by raomengnan on 17-1-19.
 */
public interface WorkflowCommandService {

    WorkflowInstanceExecutor getInstanceExecutor();
    WorkflowService getWFService();
    WorkflowExecuteStatusService getStatusService();
    WorkflowInstanceService getInstanceService();
    WorkflowLogService getLogService();
    boolean importJars(String workflowId);

}
