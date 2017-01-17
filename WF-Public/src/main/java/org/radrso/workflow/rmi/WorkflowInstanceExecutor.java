package org.radrso.workflow.rmi;

import org.radrso.workflow.entities.config.items.Step;
import org.radrso.workflow.entities.response.WFResponse;

import java.io.Serializable;

/**
 * Created by raomengnan on 17-1-4.
 */
public interface WorkflowInstanceExecutor extends Serializable{

    WFResponse execute(Step step, Object[] params, String[] paramNames);
}
