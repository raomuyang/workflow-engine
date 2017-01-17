package org.radrso.workflow.rmi;

import org.radrso.workflow.entities.response.WFResponse;

import java.io.Serializable;

/**
 * Created by raomengnan on 17-1-4.
 */
public interface WorkflowCommander extends Serializable{
    WFResponse importJar(String workflowId, String jarName, byte[] stream);
}
