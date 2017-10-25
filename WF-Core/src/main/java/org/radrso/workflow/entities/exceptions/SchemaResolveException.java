package org.radrso.workflow.entities.exceptions;

/**
 * Created by raomengnan
 * on 17-1-14.
 */
public class SchemaResolveException extends Exception {
    public static final String PARAM_RESOLVER_EXCEPTION = "Workflow schema resolve exception";

    public SchemaResolveException(String msg) {
        this(msg, null);
    }

    public SchemaResolveException(String msg, Throwable t) {
        super(String.format("%s, %s", PARAM_RESOLVER_EXCEPTION, msg), t);
    }
}
