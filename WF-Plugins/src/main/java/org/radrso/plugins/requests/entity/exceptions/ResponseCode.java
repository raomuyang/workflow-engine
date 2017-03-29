package org.radrso.plugins.requests.entity.exceptions;


import java.io.Serializable;

/**
 * Created by raomengnan on 16-12-9.
 */
public enum ResponseCode implements Serializable {


    UNKNOW(5000, "Unknow Exception"),
    BUILD_REQUEST_EXCEPTION(5001, "EXCEPTION IN BUILD REQUEST"),
    UNKNOW_HOST_EXCEPTION(5003, "UNKNOW HOST EXCEPTION"),
    UNKNOW_REQUEST_EXCEPTION(5004, "Unknow exception in request"),
    UNSUPPORTED_POTOCOL(5005, "UNSUPPORT REQUEST PORTOCOL"),
    UNSUPPORTED_REQUEST_METHOD(5006, "METHOD NOT SUPPORTED"),
    SOCKET_EXCEPTION(5007, "Exception happened when build socket connect"),

    NULL_PARAM_EXCEPTION(4031, "JSON object is null"),
    CONFIG_PARSE_ERROR(4032, "Can not resolved the params in config "),

    CLASS_NOT_FOUND(4041, "Class not found"),
    CLASS_INSTANCE_EXCEPTION(4042, "Class instance exception"),
    METHOD_NOT_FOUND(4043, "Method not found"),
    METHOD_ACCESS_ERROR(4044, "Method access error"),
    METHOD_INVOCATION_ERROR(4045, "Method access exception"),
    JAR_FILE_NOT_FOUND(4046, "Jar file not found"),
    ILLEGAL_ARGMENT_EXCEPTION(4047, "Argument type mismatch"),


    HTTP_OK(200, "HTTP_OK"),
    HTTP_REQUEST_CONTINUE(100, "Continue"),
    HTTP_BAD_REQUEST(400, "Bad Request"),
    HTTP_UNAUTHORIZED(401, "Unauthorized"),
    HTTP_FORBIDDEN(403, "Forbidden"),
    HTTP_NOT_FOUND(404, "Not Found"),
    HTTP_METHOD_NOT_ALLOWED(405, "Method Not Allowed"),
    HTTP_INTERNAL_SERVER_ERROR(500, "Internal Server Error"),
    HTTP_BAD_GATEWAY(502, "Bad Gateway"),
    HTTP_SERVICE_UNAVAILABLE(503, "Service Unavailable");

    int code;
    String info;

    ResponseCode(int c, String intro) {
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
