package org.radrso.workflow.entities.exceptions;

/**
 * Created by raomengnan
 * on 17-1-14.
 */
public class ConfigReadException extends Exception {
    public static final String PARAM_RESOLVER_EXCEPTION = "param resolver exception";

    public ConfigReadException(String msg) {
        this(msg, null);
    }

    public ConfigReadException(String msg, Throwable t) {
        super(PARAM_RESOLVER_EXCEPTION + String.format("[%s]", msg), t);
    }
}
