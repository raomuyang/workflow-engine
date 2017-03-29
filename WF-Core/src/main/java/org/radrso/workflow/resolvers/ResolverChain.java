package org.radrso.workflow.resolvers;

import org.radrso.workflow.entities.config.WorkflowConfig;
import org.radrso.workflow.entities.config.items.Step;
import org.radrso.workflow.entities.wf.WorkflowInstance;
import org.radrso.workflow.resolvers.impl.ParamsResolver;
import org.radrso.workflow.resolvers.impl.StepActionResolver;
import org.radrso.workflow.resolvers.impl.WorkflowConfigResolver;

/**
 * Created by rao-mengnan on 2017/3/16.
 */
public class ResolverChain {

    public static BaseParamsResolver getParamsResolver(WorkflowInstance instance){
        return new ParamsResolver(instance);
    }

    public static BaseWorkflowConfigResolver getWorkflowConfigResolver(WorkflowConfig workflowConfig, WorkflowInstance workflowInstance){
        return new WorkflowConfigResolver(workflowConfig, workflowInstance);
    }

    public static BaseStepActionResolver getStepActionResolver(Step step, Object[] params, String[] paramNames){
        return new StepActionResolver(step, params, paramNames);
    }
}
