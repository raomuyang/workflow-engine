package org.radrso.core;

import org.radrso.entities.config.items.Judge;
import org.radrso.entities.config.items.Step;
import org.radrso.entities.response.WFResponse;

import java.io.Serializable;

/**
 * Created by raomengnan on 17-1-4.
 */
public interface WorkflowInstanceExecutor extends Serializable{

    WorkflowInstanceExecutor doOnNext(Step n);

    WorkflowInstanceExecutor stepSubscribe(Judge conditionItem);

    WorkflowInstanceExecutor and(Step n);

    WorkflowInstanceExecutor onError(Exception e);

    WorkflowInstanceExecutor onFinish();

    WFResponse execute();
}
