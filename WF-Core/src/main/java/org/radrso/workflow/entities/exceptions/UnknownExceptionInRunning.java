package org.radrso.workflow.entities.exceptions;

/**
 * Created by raomengnan on 17-1-17.
 */
@Deprecated
public class UnknownExceptionInRunning extends Throwable{
    public UnknownExceptionInRunning(String msg){
        super(msg);
    }

    public UnknownExceptionInRunning(Throwable e){
        super(e);
    }

    public UnknownExceptionInRunning(String msg, Throwable e){
        super(msg, e);
    }
}
