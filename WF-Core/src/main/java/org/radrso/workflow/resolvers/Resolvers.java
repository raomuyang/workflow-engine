package org.radrso.workflow.resolvers;

import org.radrso.workflow.entities.config.WorkflowConfig;
import org.radrso.workflow.entities.config.items.Step;
import org.radrso.workflow.entities.wf.WorkflowInstance;
import org.radrso.workflow.internal.resolver.ParamsResolverImpl;
import org.radrso.workflow.internal.resolver.StepActionResolverImpl;
import org.radrso.workflow.internal.resolver.FlowResolverImpl;

/**
 * Created by rao-mengnan on 2017/3/16.
 */
public final class Resolvers {

    public static ParamsResolver getParamsResolver(WorkflowInstance instance){
        return new ParamsResolverImpl(instance);
    }

    public static FlowResolver getFlowResolver(WorkflowConfig workflowConfig, WorkflowInstance workflowInstance){
        return new FlowResolverImpl(workflowConfig, workflowInstance);
    }

    public static StepActionResolver getStepActionResolver(Step step, Object[] params, String[] paramNames){
        return new StepActionResolverImpl(step, params, paramNames);
    }
}
