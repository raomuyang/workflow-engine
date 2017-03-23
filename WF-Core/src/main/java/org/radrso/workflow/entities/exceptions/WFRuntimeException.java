package org.radrso.workflow.entities.exceptions;

import lombok.Data;

/**
 * Created by raomengnan on 17-1-16.
 */
@Data
public class WFRuntimeException extends RuntimeException{
    public static final String WORKFLOW_EXPIRED = "Workflow Expired";
    public static final String NO_SUCH_WORKFLOW_STATUS = "No Such Workflow Status";
    public static final String JAR_FILE_NO_FOUND = "Not found jar file";

    public WFRuntimeException(){}

    public WFRuntimeException(String msg){
        super(msg);
    }

    public WFRuntimeException(String message, Throwable e) {
        super(message, e);
    }
}
