package org.radrso.workflow.constant;


import java.io.Serializable;

/**
 * Created by raomengnan on 16-12-9.
 */
public enum ExceptionCode implements Serializable {


    UNKNOW(5000, "Unknow Exception"),
    UNSUPPORTED_REQUEST_METHOD(5006, "METHOD NOT SUPPORTED"),
    SOCKET_EXCEPTION(5007, "Exception happened when build socket connect"),
    INTERRUPT_EXCEPTION(5008, "Interrupt exception"),

    NULL_PARAM_EXCEPTION(4031, "JSON object is null"),
    CONFIG_PARSE_ERROR(4032, "Can not resolved the params in config "),

    CLASS_NOT_FOUND(4041, "Class not found"),
    CLASS_INSTANCE_EXCEPTION(4042, "Class instance exception"),
    METHOD_NOT_FOUND(4043, "Method not found"),
    METHOD_ACCESS_ERROR(4044, "Method access error"),
    METHOD_INVOCATION_ERROR(4045, "Method access exception"),
    JAR_FILE_NOT_FOUND(4046, "Jar file not found"),
    ILLEGAL_ARGMENT_EXCEPTION(4047, "Argument type mismatch");

    int code;
    String info;

    ExceptionCode(int c, String intro) {
        this.code = c;
        this.info = intro;
    }

    public int code() {
        return code;
    }

    public String info() {
        return this.info;
    }
}
