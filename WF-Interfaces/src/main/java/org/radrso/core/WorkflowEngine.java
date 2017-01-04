package org.radrso.core;

import org.radrso.entities.response.WFResponse;

/**
 * Created by raomengnan on 17-1-4.
 */
public interface WorkflowEngine {
    WFResponse start();
    WFResponse getInstanse();
    WFResponse getWFStatus();

    WFResponse getBaseInfo();
}
