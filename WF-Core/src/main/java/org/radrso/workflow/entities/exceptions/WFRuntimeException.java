package org.radrso.workflow.entities.exceptions;

/**
 * Created by raomengnan on 17-1-16.
 */
public class WFRuntimeException extends RuntimeException{
    public static final String WORKFLOW_EXPIRED = "Workflow Expired";
    public static final String NO_SUCH_WORKFLOW_STATUS = "No Such Workflow Status";
    public static final String JAR_FILE_NO_FOUND = "Not found jar file";

    private int code;

    public WFRuntimeException(String msg, int code){
        super(msg);
        this.code = code;
    }

    public WFRuntimeException(String message, Throwable e, int code) {
        super(message, e);
        this.code = code;
    }

    @Override
    public String toString(){
        return String.format("[%s] ", code) + super.toString();
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
