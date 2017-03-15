package org.radrso.plugins.requests.entity.exceptions;


/**
 * Created by raomengnan on 16-12-9.
 */
public class BaseException extends Exception {
    protected ResponseCode code;

    public BaseException() {
        super();
    }

    public BaseException(ResponseCode code) {
        this(code.info(), code);
    }

    public BaseException(String message, ResponseCode code) {
        super(message);
        this.code = code;
    }

    public BaseException(ResponseCode code, Throwable cause) {
        super(cause);
        this.code = code;
    }

    public BaseException(String message, ResponseCode code, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public ResponseCode getCode() {
        return code;
    }

    public void setCode(ResponseCode code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return super.toString() + "," + code.code();
    }
}
