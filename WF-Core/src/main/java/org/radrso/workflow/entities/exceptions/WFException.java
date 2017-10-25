package org.radrso.workflow.entities.exceptions;

import org.radrso.workflow.constant.WFStatusCode;

/**
 * Created by Rao-Mengnan
 * on 17-1-16.
 */
public class WFException extends RuntimeException implements WFError {
    protected int code;

    public WFException(String msg) {
        this(WFStatusCode.UNKNOWN.code(), msg);
    }

    public WFException(WFStatusCode errorCode) {
        this(errorCode.code(), errorCode.info());
    }

    public WFException(int code, String msg) {
        this(code, msg, null);
    }

    public WFException(WFStatusCode errorCode, String msg) {
        this(errorCode.code(), msg, null);
    }

    public WFException(WFException e) {
        this(e.getCode() ,e.getDetailMessage(), e.getCause());
    }

    public WFException(int code, String msg, Throwable t) {
        super(msg, t);
        this.code = code;
    }

    @Override
    public String getDetailMessage() {
        return super.getMessage();
    }

    @Override
    public int getCode() {
        return this.code;
    }

    @Override
    public String getMessage() {
        return String.format("[CODE] %s\n[MESSAGE] %s", code, super.getMessage());
    }

    @Override
    public String toString() {
        return getMessage();
    }
}
