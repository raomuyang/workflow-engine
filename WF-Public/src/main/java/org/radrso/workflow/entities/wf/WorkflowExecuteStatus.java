package org.radrso.workflow.entities.wf;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by raomengnan on 17-1-16.
 */
@Data
public class WorkflowExecuteStatus implements Serializable{
    public static final String START = "started";
    public static final String STOP = "stopped";
    public static final String EXCEPTION = "exception";
    private String workflowId;
    private String status;
    private String msg;
}
