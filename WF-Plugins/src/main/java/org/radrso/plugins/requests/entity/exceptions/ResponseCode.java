package org.radrso.plugins.requests.entity.exceptions;


/**
 * Created by raomengnan on 16-12-9.
 */
public enum ResponseCode {
    OK(200, "OK"),

    UNKNOW(5000, "Unknow Exception"),
    BUILD_REQUEST_EXCEPTION(5001, "EXCEPTION IN BUILD REQUEST"),
    PARAM_NULL_EXCEPTION(5002, "JSON object is null"),
    UNKNOW_HOST_EXCEPTION(5003, "UNKNOW HOST EXCEPTION"),
    UNKNOW_REQUEST_EXCEPTION(5004, "Unknow exception in request"),
    UNSUPPORTED_POTOCOL(5005, "UNSUPPORT REQUEST PORTOCOL"),
    UNSUPPORTED_REQUEST_METHOD(5006, "METHOD NOT SUPPORTED"),
    SOCKET_EXCEPTION(5007, "Exception happened when build socket connect"),


    BAD_REQUEST(400, "Bad Request"),
    UNAUTHORIZED(401, "Unauthorized"),
    FORBIDDEN(403, "Forbidden"),
    NOT_FOUND(404, "Not Found"),
    METHOD_NOT_ALLOWED(405, "Method Not Allowed"),
    INTERNAL_SERVER_ERROR(500, "Internal Server Error"),
    BAD_GATEWAY(502, "Bad Gateway"),
    SERVICE_UNAVAILABLE(503, "Service Unavailable");

    int code;
    String info;
    ResponseCode(int c, String intro) {
        this.code = c;
        this.info = intro;
    }

    public int code(){return code;}
    public String info(){return this.info;}
}
