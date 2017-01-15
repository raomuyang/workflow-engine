package org.radrso.workflow.provider.requests.entity.exceptions.impl;

import org.radrso.workflow.provider.requests.entity.exceptions.ResponseCode;
import org.radrso.workflow.provider.requests.entity.exceptions.WFException;

/**
 * Created by raomengnan on 16-12-9.
 */

public class RequestException extends WFException {
    public RequestException(){
        super();
    }

    public RequestException(ResponseCode code){
        super( code);
    }

    public RequestException(String msg, ResponseCode code){
        super(msg, code);
    }

    public RequestException(String msg, ResponseCode code, Throwable cause){
        super(msg, code, cause);
    }

    public RequestException(ResponseCode code, Throwable cause) {
        super(code, cause);
    }
}
