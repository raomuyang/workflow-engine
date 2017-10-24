package org.radrso.workflow.constant;

import org.radrso.plugins.requests.entity.MethodEnum;
import org.radrso.plugins.requests.entity.ResponseCode;
import org.radrso.plugins.requests.entity.exceptions.impl.RequestException;

/**
 * Created by raomengnan on 17-1-15.
 */
public class RequestMethodMapping {
    public static MethodEnum getMethod(String name) throws RequestException {
        name = String.valueOf(name).toLowerCase();
        switch (name){
            case "get": return MethodEnum.GET;
            case "post": return MethodEnum.POST;
            case "put": return MethodEnum.PUT;
            case "delete": return MethodEnum.DELETE;
            default:
                throw new RequestException(ResponseCode.UNSUPPORTED_REQUEST_METHOD);
        }
    }
}
