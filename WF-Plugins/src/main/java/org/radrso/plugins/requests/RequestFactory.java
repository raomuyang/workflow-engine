package org.radrso.plugins.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.http.entity.ContentType;
import org.radrso.plugins.requests.entity.Method;
import org.radrso.plugins.requests.entity.exceptions.ResponseCode;
import org.radrso.plugins.requests.entity.exceptions.impl.RequestException;

import java.lang.reflect.Constructor;
import java.util.Map;

/**
 * Created by raomengnan on 16-12-9.
 */
@Data
@AllArgsConstructor
public class RequestFactory {

    private String url;
    private Method method = Method.GET;
    private Map<String, String> headers;
    private Object params;
    private ContentType contentType;
    private boolean usePool;

    public static Request createRequest(String url, Method method, Map<String, Object> headers,
                                        Object params, ContentType contentType, Boolean usePool) throws RequestException {
        String protocol = url.substring(0,url.indexOf(":"));
        Constructor<Request> constructor = getConstructor(protocol);
        Request request;

        try {
            request = constructor.newInstance(url, method, headers, params, contentType, usePool);
        } catch (ReflectiveOperationException e) {
            throw new RequestException(ResponseCode.UNSUPPORTED_REQUEST_METHOD, e);
        } catch (Exception e){
            throw new RequestException(ResponseCode.BUILD_REQUEST_EXCEPTION ,e);
        }

        return request;
    }


    private static Constructor<Request> getConstructor(String portocol) throws RequestException {
        portocol = portocol.toLowerCase();
        char[] cs = portocol.toCharArray();
        cs[0] -= 32;
        portocol = new String(cs);

        String name = String.format("org.radrso.plugins.requests.impl.%sRequest", portocol);
        try {

            Class clazz = Class.forName(name);

            Constructor constructor = clazz.getConstructor(
                    String.class,
                    Method.class,
                    Map.class,
                    Object.class,
                    ContentType.class,
                    Boolean.class
            );
            return constructor;
        }catch (Exception e){
            throw new RequestException(ResponseCode.UNSUPPORTED_POTOCOL, e);
        }

    }

}
