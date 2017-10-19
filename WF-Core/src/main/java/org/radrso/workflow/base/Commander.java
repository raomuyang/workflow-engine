package org.radrso.workflow.base;

import org.radrso.workflow.entities.schema.WorkflowSchema;
import org.radrso.workflow.entities.schema.items.Step;
import org.radrso.workflow.entities.response.WFResponse;
import org.radrso.workflow.entities.wf.WorkflowErrorLog;
import org.radrso.workflow.entities.wf.WorkflowInstance;

import java.util.List;

/**
 * Created by raomengnan on 17-1-19.
 */
public interface Commander {

    boolean jarFilesSync(String workflowId);

    boolean saveErrorLog(WorkflowErrorLog log);

    boolean updateInstance(WorkflowInstance instance);

    WorkflowSchema getWorkflowConfig(String instanceId);

    String getWorkflowStatus(String workflowId);

    WFResponse runStepAction(Step step, Object[] params, String[] paramNames);

    WorkflowInstance getInstance(String instanceId);

    static boolean isDefinedJarsFiles(WorkflowSchema workflowConfig) {
        if (workflowConfig == null)
            return false;
        List<String> jars = workflowConfig.getJars();
        if (jars == null || jars.size() == 0)
            return false;
        return true;
    }


}
