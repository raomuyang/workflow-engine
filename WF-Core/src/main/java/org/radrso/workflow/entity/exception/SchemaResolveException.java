package org.radrso.workflow.entity.exception;

import org.radrso.workflow.constant.WFStatusCode;

/**
 * Created by raomengnan
 * on 17-1-14.
 */
public class SchemaResolveException extends Exception implements WFError {
    private static final String PARAM_RESOLVER_EXCEPTION = "Workflow schema resolve exception";

    protected int code = WFStatusCode.SCHEMA_PARSE_ERROR.code();

    public SchemaResolveException(String msg) {
        this(msg, null);
    }

    public SchemaResolveException(String msg, Throwable t) {
        super(String.format("%s, %s", PARAM_RESOLVER_EXCEPTION, msg), t);
    }

    public SchemaResolveException(int code, String msg) {
        this(code, msg, null);
    }

    public SchemaResolveException(int code, String msg, Throwable t) {
        this(msg, t);
        this.code = code;
    }


    @Override
    public int getCode() {
        return this.code;
    }

    @Override
    public String getDetailMessage() {
        return super.getMessage();
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
