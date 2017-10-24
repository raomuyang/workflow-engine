package org.radrso.workflow.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Rao-Mengnan
 * on 16-12-9.
 */
public enum  WFErrorCode {

    INTERRUPT_EXCEPTION(3040, "Interrupt exception"),

    NULL_PARAM_EXCEPTION(4001, "JSON object is null"),
    CONFIG_PARSE_ERROR(4002, "Can not resolved the params in schema "),
    UNSUPPORTED_REQUEST_METHOD(4003, "METHOD NOT SUPPORTED"),

    CLASS_NOT_FOUND(4041, "Invoke class not found"),
    CLASS_INSTANCE_EXCEPTION(4042, "Target class failed to initialize"),
    METHOD_NOT_FOUND(4043, "Target method not found"),
    METHOD_ACCESS_ERROR(4044, "Target method access error"),
    METHOD_INVOCATION_ERROR(4045, "Target method access exception"),
    JAR_FILE_NOT_FOUND(4046, "Target jar file not found"),
    ILLEGAL_ARGUMENT_EXCEPTION(4047, "Argument type mismatch with target method"),

    UNKNOWN(5000, "Unknown Exception"),
    SOCKET_EXCEPTION(5007, "Exception happened when build socket connect");

    int code;
    String info;
    WFErrorCode(int code, String info) {
        this.code = code;
        this.info = info;
    }

    public int code() {
        return code;
    }

    public String info() {
        return this.info;
    }
}
