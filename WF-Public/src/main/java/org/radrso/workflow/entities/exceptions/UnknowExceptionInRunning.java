package org.radrso.workflow.entities.exceptions;

/**
 * Created by raomengnan on 17-1-17.
 */
public class UnknowExceptionInRunning extends Throwable{
    public UnknowExceptionInRunning(String msg){
        super(msg);
    }

    public UnknowExceptionInRunning(Throwable e){
        super(e);
    }

    public UnknowExceptionInRunning(String msg, Throwable e){
        super(msg, e);
    }
}
