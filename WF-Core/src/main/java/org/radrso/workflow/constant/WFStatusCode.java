package org.radrso.workflow.constant;

/**
 * Created by Rao-Mengnan
 * on 16-12-9.
 */
public enum WFStatusCode {

    OK(200, "HTTP_OK"),

    HTTP_REQUEST_CONTINUE(100, "Continue"),
    HTTP_BAD_REQUEST(400, "Bad Request"),
    HTTP_UNAUTHORIZED(401, "Unauthorized"),
    HTTP_FORBIDDEN(403, "Forbidden"),
    HTTP_NOT_FOUND(404, "Not Found"),
    HTTP_METHOD_NOT_ALLOWED(405, "Method Not Allowed"),
    HTTP_INTERNAL_SERVER_ERROR(500, "Internal Server Error"),
    HTTP_BAD_GATEWAY(502, "Bad Gateway"),
    HTTP_SERVICE_UNAVAILABLE(503, "Service Unavailable"),

    INTERRUPT_EXCEPTION(3040, "Interrupt exception"),

    NULL_PARAM_EXCEPTION(4001, "JSON object is null"),
    SCHEMA_PARSE_ERROR(4002, "Can not resolved the params in schema "),
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
    WFStatusCode(int code, String info) {
        this.code = code;
        this.info = info;
    }

    public int code() {
        return code;
    }

    public String info() {
        return this.info;
    }

    public static boolean isOK(int code) {
        return code / 100 == 2;
    }
}
