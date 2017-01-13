package org.radrso.core;

import org.radrso.entities.config.items.Judge;
import org.radrso.entities.config.items.Step;
import org.radrso.entities.response.WFResponse;

/**
 * Created by raomengnan on 17-1-4.
 */
public interface WorkflowInstance {

    WorkflowInstance doOnNext(Step n);

    WorkflowInstance stepSubscribe(Judge conditionItem);

    WorkflowInstance and(Step n);

    WorkflowInstance onError(Exception e);

    WorkflowInstance onFinish();

    WFResponse execute();
}
