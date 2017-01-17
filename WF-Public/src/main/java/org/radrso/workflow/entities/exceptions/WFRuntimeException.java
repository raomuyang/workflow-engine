package org.radrso.workflow.entities.exceptions;

/**
 * Created by raomengnan on 17-1-16.
 */
public class WFRuntimeException extends RuntimeException{
    public WFRuntimeException(String msg){
        super(msg);
    }

    public WFRuntimeException(String message, Throwable e) {
        super(message, e);
    }
}
