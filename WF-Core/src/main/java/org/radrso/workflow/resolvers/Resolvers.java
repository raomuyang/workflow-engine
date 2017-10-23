package org.radrso.workflow.resolvers;

import org.radrso.workflow.entities.schema.WorkflowSchema;
import org.radrso.workflow.entities.schema.items.Step;
import org.radrso.workflow.entities.model.WorkflowInstance;

/**
 * Created by rao-mengnan on 2017/3/16.
 */
public final class Resolvers {

    public static SchemaResolver getParamsResolver(WorkflowInstance instance){
        return new SchemaResolverImpl(instance);
    }

    public static WorkflowResolver getFlowResolver(WorkflowSchema workflowConfig, WorkflowInstance workflowInstance){
        return new WorkflowResolverImpl(workflowConfig, workflowInstance);
    }

    public static RequestResolver getStepActionResolver(Step step, Object[] params, String[] paramNames){
        return new RequestResolverImpl(step, params, paramNames);
    }
}
