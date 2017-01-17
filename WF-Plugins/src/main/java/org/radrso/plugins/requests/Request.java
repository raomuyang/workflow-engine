package org.radrso.plugins.requests;

import lombok.Data;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.*;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.radrso.plugins.requests.entity.Method;
import org.radrso.plugins.requests.entity.Response;
import org.radrso.plugins.requests.entity.exceptions.ResponseCode;
import org.radrso.plugins.requests.entity.exceptions.impl.RequestException;

import java.io.IOException;

/**
 * Created by raomengnan on 16-12-9.
 */
@Data
public abstract class Request {
    protected String url;
    protected Method method;
    protected Object params;
    protected ContentType contentType;
    protected CloseableHttpClient client;
    private HttpRequestBase requestBase;
    private boolean usePool;

    public Request(String url, Method method, Object params, ContentType contentType, HttpRequestBase requestBase, Boolean usePool){
        this.url = url;
        this.method = method;
        this.params = params;
        this.contentType = contentType;
        this.requestBase = requestBase;
        this.usePool = usePool;
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
                    throw new RequestException(ResponseCode.HTTP_BAD_REQUEST);
                case 401:
                    throw new RequestException(ResponseCode.HTTP_UNAUTHORIZED);
                case 403:
                    throw new RequestException(ResponseCode.HTTP_FORBIDDEN);
                case 404:
                    throw new RequestException(ResponseCode.HTTP_NOT_FOUND);
                case 405:
                    throw new RequestException(ResponseCode.HTTP_METHOD_NOT_ALLOWED);
                case 500:
                    throw new RequestException(ResponseCode.HTTP_INTERNAL_SERVER_ERROR);
                case 502:
                    throw new RequestException(ResponseCode.HTTP_BAD_GATEWAY);
                case 503:
                    throw new RequestException(ResponseCode.HTTP_SERVICE_UNAVAILABLE);
                default:
                    if(response.getStatusCode() / 100 == 4)
                        throw new RequestException(ResponseCode.UNKNOW_REQUEST_EXCEPTION);
                    else if(response.getStatusCode() / 100 == 5)
                        throw new RequestException(ResponseCode.UNKNOW_HOST_EXCEPTION);

                    else
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
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

    private void initClient(){
        if(usePool)
            client = buildClientUsePool();
        else
            client = buildClient();
    }

    public abstract CloseableHttpClient buildClient();
    public abstract CloseableHttpClient buildClientUsePool();
    public abstract void signature(HttpRequestBase requestBase);
    public abstract void closeConnectionPool();

}
