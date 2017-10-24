package org.radrso.workflow.entities.exceptions;

import org.radrso.workflow.constant.WFErrorCode;

/**
 * Created by Rao-Mengnan
 * on 2017/10/24.
 */
public class WFException extends RuntimeException {
    protected int code;

    public WFException(String msg) {
        this(WFErrorCode.UNKNOWN.code(), msg);
    }

    public WFException(WFErrorCode errorCode) {
        this(errorCode.code(), errorCode.info());
    }

    public WFException(int code, String msg) {
        this(code, msg, null);
    }

    public WFException(WFErrorCode errorCode, String msg) {
        this(errorCode.code(), msg, null);
    }

    public WFException(WFException e) {
        this(e.getCode() ,e.getDetailMessage(), e.getCause());
    }

    public WFException(int code, String msg, Throwable t) {
        super(msg, t);
        this.code = code;
    }

    public String getDetailMessage() {
        return super.getMessage();
    }

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
