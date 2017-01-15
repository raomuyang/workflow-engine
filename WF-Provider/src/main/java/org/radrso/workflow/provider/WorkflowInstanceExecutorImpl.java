package org.radrso.workflow.provider;

import org.radrso.workflow.rmi.WorkflowInstanceExecutor;
import org.radrso.workflow.resolvers.StepExecuteResolver;
import org.radrso.workflow.entities.config.items.Step;
import org.radrso.workflow.entities.response.WFResponse;

import org.springframework.stereotype.Service;

/**
 * Created by raomengnan on 17-1-5.
 */

@Service("WorkflowInstanceExecutor")
public class WorkflowInstanceExecutorImpl implements WorkflowInstanceExecutor {

    @Override
    public WFResponse execute(Step step, Object[] params, String[] paramNames) {

        StepExecuteResolver resolver = new StepExecuteResolver(step, params, paramNames);
        String protocol = step.getCall().substring(0,step.getCall().indexOf(":"));

        WFResponse response = null;
        if(protocol.toLowerCase().indexOf("http") >= 0)
            response = resolver.netReuqest();
        else
            response = resolver.classRequest();
        return response;
    }


}
