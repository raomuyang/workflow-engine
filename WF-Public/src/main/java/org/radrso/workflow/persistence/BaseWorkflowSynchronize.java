package org.radrso.workflow.persistence;

import org.radrso.workflow.entities.config.WorkflowConfig;
import org.radrso.workflow.entities.config.items.Step;
import org.radrso.workflow.entities.response.WFResponse;
import org.radrso.workflow.entities.wf.WorkflowErrorLog;
import org.radrso.workflow.entities.wf.WorkflowInstance;

import java.util.List;

/**
 * Created by raomengnan on 17-1-19.
 */
public interface BaseWorkflowSynchronize {

    boolean importJars(String workflowId);

    boolean logError(WorkflowErrorLog log);

    boolean updateInstance(WorkflowInstance instance);

    WorkflowConfig getWorkflow(String instanceId);

    String getWorkflowStatus(String workflowId);

    WFResponse startStep(Step step, Object[] params, String[] paramNames);

    static boolean isDefinedJarsFiles(WorkflowConfig workflowConfig) {
        if (workflowConfig == null)
            return false;
        List<String> jars = workflowConfig.getJars();
        if (jars == null || jars.size() == 0)
            return false;
        return true;
    }
}
