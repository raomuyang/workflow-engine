package org.radrso;

import org.radrso.workflow.provider.requests.entity.Method;
import org.radrso.workflow.provider.requests.entity.exceptions.ResponseCode;
import org.radrso.workflow.provider.requests.entity.exceptions.impl.RequestException;

/**
 * Created by raomengnan on 17-1-15.
 */
public class RequestMethodMapping {
    public static Method getMethod(String name) throws RequestException {
        name = name.toLowerCase();
        switch (name){
            case "get": return Method.GET;
            case "post": return Method.POST;
            case "put": return Method.PUT;
            case "delete": return Method.DELETE;
            default:
                throw new RequestException(ResponseCode.UNSUPPORTED_REQUEST_METHOD);
        }
    }
}
