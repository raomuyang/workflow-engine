package org.radrso.plugins.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.http.client.methods.*;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.radrso.plugins.requests.entity.Method;
import org.radrso.plugins.requests.entity.exceptions.ResponseCode;
import org.radrso.plugins.requests.entity.exceptions.impl.RequestException;

import java.lang.reflect.Constructor;

/**
 * Created by raomengnan on 16-12-9.
 */
@Data
@AllArgsConstructor
public class RequestFactory {

    private String url;
    private Method method = Method.GET;
    private Object params;
    private ContentType contentType;
    private boolean usePool;

    public Request createRequest() throws RequestException {
        String protocol = url.substring(0,url.indexOf(":"));
        return createRequest(protocol);
    }

    public Request createRequest(String protocol) throws RequestException {
        Constructor<Request> constructor = getConstructor(protocol);

        try {
            HttpRequestBase requestBase = null;
            switch (method){
                case GET:
                    requestBase = buildGet();break;
                case POST:
                    requestBase = buildPost();break;
                case PUT:
                    requestBase = buildPut();break;
                case DELETE:
                    requestBase = buildDelete();break;
                default:
                    throw new RequestException(ResponseCode.UNSUPPORTED_REQUEST_METHOD);
            }
            Request request = constructor.newInstance(url, method, params, contentType, requestBase, usePool);
            return request;
        } catch (ReflectiveOperationException e) {
            throw new RequestException(ResponseCode.UNSUPPORTED_POTOCOL, e);
        }
    }

    protected HttpRequestBase buildGet()throws RequestException {
        try {
            return new HttpGet(this.getUrl());
        }catch (Exception e){
            throw new RequestException(ResponseCode.BUILD_REQUEST_EXCEPTION ,e);
        }
    }

    protected HttpRequestBase buildPost() throws RequestException{
        try {
            if (getParams() == null)
                throw new RequestException("Request param is null", ResponseCode.PARAM_NULL_EXCEPTION);

            StringEntity entity = new StringEntity(params.toString(), contentType);
            HttpPost post = new HttpPost(this.getUrl());
            post.setEntity(entity);
            post.setHeader("content_type", entity.getContentType().getValue());
            return post;
        }catch (Exception e){
            throw new RequestException(ResponseCode.BUILD_REQUEST_EXCEPTION ,e);
        }
    }

    protected HttpRequestBase buildPut() throws RequestException{
        try {
            if (getParams() == null)
                throw new RequestException("Request param is null", ResponseCode.PARAM_NULL_EXCEPTION);

            StringEntity entity = new StringEntity(params.toString(), contentType);
            HttpPut put = new HttpPut(this.getUrl());
            put.setEntity(entity);
            put.setHeader("content_type", entity.getContentType().getValue());
            return put;
        }catch (Exception e){
            throw new RequestException(ResponseCode.BUILD_REQUEST_EXCEPTION ,e);
        }
    }

    protected HttpRequestBase buildDelete() throws RequestException{
        try {
            return new HttpDelete(this.getUrl());
        }catch (Exception e){
            throw new RequestException(ResponseCode.BUILD_REQUEST_EXCEPTION ,e);
        }
    }

    private Constructor<Request> getConstructor(String portocol) throws RequestException {
        portocol = portocol.toLowerCase();
        char[] cs = portocol.toCharArray();
        cs[0] -= 32;
        portocol = new String(cs);

        String name = String.format("org.radrso.plugins.requests.org.radrso.demo.impl.%sRequest", portocol);

        try {

            Class clazz = Class.forName(name);

            Constructor<Request> constructor = clazz.getConstructor(
                    String.class,
                    Method.class,
                    Object.class,
                    ContentType.class,
                    HttpRequestBase.class,
                    Boolean.class
            );
            return constructor;
        }catch (Exception e){
            throw new RequestException(ResponseCode.UNSUPPORTED_POTOCOL, e);
        }

    }

}
