package org.radrso.workflow.entities.exceptions;

/**
 * Created by raomengnan on 17-1-14.
 */
public class ConfigReadException extends Exception{
    public static final String PARAM_RESOLVER_EXCEPTION = "param resolver exception";
    private String msg;
    public ConfigReadException(String msg){
        super(PARAM_RESOLVER_EXCEPTION + String.format("[%s]", msg));
    }
}
