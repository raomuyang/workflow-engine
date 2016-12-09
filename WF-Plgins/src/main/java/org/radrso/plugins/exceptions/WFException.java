package org.radrso.plugins.exceptions;

import lombok.Data;


/**
 * Created by raomengnan on 16-12-9.
 */
public class WFException extends Exception {
    protected WFErrorCode code;

    public WFException(){
        super();
    }

    public WFException(WFErrorCode code){
        this(code.info(), code);
    }

    public WFException(String message, WFErrorCode code){
        super(message);
        this.code = code;
    }

    public WFException(WFErrorCode code, Throwable cause){
        super(cause);
        this.code = code;
    }
    public WFException(String message, WFErrorCode code, Throwable cause){
        super(message, cause);
        this.code = code;
    }

    public WFErrorCode getCode() {
        return code;
    }

    public void setCode(WFErrorCode code) {
        this.code = code;
    }

    @Override
    public String toString(){
        return super.toString() + "," + code.code();
    }
}
