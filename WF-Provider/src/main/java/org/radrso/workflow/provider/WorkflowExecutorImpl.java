package org.radrso.workflow.provider;

import org.radrso.workflow.exec.StepCommander;
import org.radrso.workflow.rmi.WorkflowExecutor;
import org.radrso.workflow.entities.config.items.Step;
import org.radrso.workflow.entities.response.WFResponse;

import org.springframework.stereotype.Service;

/**
 * Created by raomengnan on 17-1-5.
 */

@Service
public class WorkflowExecutorImpl implements WorkflowExecutor {

    @Override
    public WFResponse execute(Step step, Object[] params, String[] paramNames) {

        return StepCommander.execute(step, params, paramNames);
    }


}
