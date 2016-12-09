package org.radrso.plugins.exceptions.impl;

import org.radrso.plugins.exceptions.WFErrorCode;
import org.radrso.plugins.exceptions.WFException;

/**
 * Created by raomengnan on 16-12-9.
 */

public class RequestException extends WFException {
    public RequestException(){
        super();
    }

    public RequestException(WFErrorCode code){
        super( code);
    }

    public RequestException(String msg, WFErrorCode code){
        super(msg, code);
    }

    public RequestException(String msg, WFErrorCode code, Throwable cause){
        super(msg, code, cause);
    }

    public RequestException(WFErrorCode code, Throwable cause) {
        super(code, cause);
    }
}
