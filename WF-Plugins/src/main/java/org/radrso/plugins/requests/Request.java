package org.radrso.plugins.requests;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import lombok.Data;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.*;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.radrso.plugins.requests.entity.Method;
import org.radrso.plugins.requests.entity.Response;
import org.radrso.plugins.requests.entity.exceptions.ResponseCode;
import org.radrso.plugins.requests.entity.exceptions.impl.RequestException;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;
import java.util.Map;

/**
 * Created by raomengnan on 16-12-9.
 */
@Data
public abstract class Request {
    public static ContentType APPLICATION_FORM_UTF8 = ContentType.create(ContentType.APPLICATION_FORM_URLENCODED.getMimeType(), Charset.forName("utf-8"));
    protected String url;
    protected Method method;
    protected Object params;
    protected Map<String, Object> headers;
    protected ContentType contentType;
    protected CloseableHttpClient client;
    private HttpRequestBase requestBase;
    private boolean usePool;

    public Request(String url, Method method, Map<String, Object> headers,
                   Object params, ContentType contentType, Boolean usePool) throws ReflectiveOperationException {
        this.url = url;
        this.method = method;
        this.headers = headers;
        this.params = params;
        this.contentType = contentType == null?ContentType.APPLICATION_JSON:contentType;
        this.usePool = usePool;

        this.requestBase = buildRequestBase(method.value());
    }

    public Response sendRequest() throws RequestException {
        Response response = null;
        int retry = 3;
        boolean exceptionHappened = false;
        while (retry-- > 0 && !exceptionHappened){
            exceptionHappened = false;

            try {
                if(client == null)
                    initClient();
                signature(requestBase);

                HttpResponse httpResponse = client.execute(requestBase);
                response = new Response(httpResponse);
                requestBase.abort();
            } catch (IOException e) {
                exceptionHappened = true;
                continue;
            } catch (IllegalStateException e){
                throw new RequestException(e.getMessage(), ResponseCode.UNKNOW_HOST_EXCEPTION, e);
            } finally {
                if (client != null){
                    try {
                        client.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    client = null;
                }
            }
            if(response.getStatusCode() / 100 == 2)
                return response;

            switch (response.getStatusCode()){
                case 400:
                    throw new RequestException(response.getContent(), ResponseCode.HTTP_BAD_REQUEST);
                case 401:
                    throw new RequestException(response.getContent(), ResponseCode.HTTP_UNAUTHORIZED);
                case 403:
                    throw new RequestException(response.getContent(), ResponseCode.HTTP_FORBIDDEN);
                case 404:
                    throw new RequestException(response.getContent(), ResponseCode.HTTP_NOT_FOUND);
                case 405:
                    throw new RequestException(response.getContent(), ResponseCode.HTTP_METHOD_NOT_ALLOWED);
                case 500:
                    throw new RequestException(response.getContent(), ResponseCode.HTTP_INTERNAL_SERVER_ERROR);
                case 502:
                    throw new RequestException(response.getContent(), ResponseCode.HTTP_BAD_GATEWAY);
                case 503:
                    throw new RequestException(response.getContent(), ResponseCode.HTTP_SERVICE_UNAVAILABLE);
                default:
                    switch (response.getStatusCode() / 100){
                        case 3:
                        case 4: throw new RequestException(response.getContent(), ResponseCode.UNKNOW_REQUEST_EXCEPTION);
                        case 5: throw new RequestException(response.getContent(), ResponseCode.UNKNOW_HOST_EXCEPTION);
                        default:
                            try {
                                    Thread.sleep(100);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }

                    }
            }
        }

        if(retry <= 0){
            if(exceptionHappened)
                throw new RequestException(ResponseCode.SOCKET_EXCEPTION);
            else
                throw new RequestException(ResponseCode.UNKNOW);
        }
        return null;


    }

    public HttpRequestBase buildRequestBase(String method) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        char[] str = method.toLowerCase().toCharArray();
        str[0] = (char)(str[0] -32);
        method = String.valueOf(str);

        Class clazz = Class.forName("org.apache.http.client.methods.Http" + method);
        this.initNormalRequestParam(clazz);
        Constructor<HttpRequestBase> constructor = clazz.getConstructor(String.class);
        requestBase = constructor.newInstance(url);
        if(params != null && HttpEntityEnclosingRequestBase.class.isAssignableFrom(clazz)){
            StringEntity entity = new StringEntity(params.toString(), contentType);
            ((HttpEntityEnclosingRequestBase) requestBase).setEntity(entity);
        }

        this.initRequestHeader();
        return requestBase;
    }

    private void initClient(){
        if(usePool)
            client = buildClientUsePool();
        else
            client = buildClient();
    }

    private void initNormalRequestParam(Class requestBaseClass){
        if(params == null)
            return;
        if(!HttpEntityEnclosingRequestBase.class.isAssignableFrom(requestBaseClass)){

            Map<String, Object> paramsMap = null;
            if(JsonElement.class.isAssignableFrom(params.getClass()))
                paramsMap = new Gson().fromJson((JsonElement)params, Map.class);
            else if(Map.class.isAssignableFrom(params.getClass()))
                paramsMap = (Map<String, Object>) params;
            if(paramsMap != null) {
                String paramStr = "";
                for (String key : paramsMap.keySet()) {
                    if(paramStr.equals(""))
                        paramStr += "?";
                    else
                        paramStr += "&";
                    paramStr += key + "=" + paramsMap.get(key);
                }
                url += paramStr;
            }
        }
    }
    private void initRequestHeader(){
        if(headers.get("Content-Type") != null)
            headers.put("Content-Type", contentType.toString());
        for(String key: headers.keySet())
            requestBase.setHeader(key, String.valueOf(headers.get(key)));



    }

    public abstract CloseableHttpClient buildClient();
    public abstract CloseableHttpClient buildClientUsePool();
    public abstract void signature(HttpRequestBase requestBase);
    public abstract void closeConnectionPool();


}
