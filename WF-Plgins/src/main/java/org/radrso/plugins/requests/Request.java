package org.radrso.plugins.requests;

import lombok.Data;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.*;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.radrso.plugins.exceptions.WFErrorCode;
import org.radrso.plugins.exceptions.impl.RequestException;
import org.radrso.plugins.requests.entity.Method;
import org.radrso.plugins.requests.entity.Response;

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

    public Request(String url, Method method, Object params, ContentType contentType, HttpRequestBase requestBase){
        this.url = url;
        this.method = method;
        this.params = params;
        this.contentType = contentType;
        this.requestBase = requestBase;
    }

    public Response sendRequest() throws RequestException {
        Response response = null;
        int retry = 3;
        boolean exceptionHappened = false;
        while (retry-- > 0 && !exceptionHappened){

            if(client == null)
                client = buildClient();

            signature(requestBase);

            exceptionHappened = false;
            try {
                HttpResponse httpResponse = client.execute(requestBase);
                response = new Response(httpResponse);
                requestBase.abort();
            } catch (IOException e) {
                exceptionHappened = true;
                continue;
            } catch (IllegalStateException e){
                throw new RequestException(e.getMessage(), WFErrorCode.UNKNOW_HOST_EXCEPTION, e);
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
                    throw new RequestException(WFErrorCode.BAD_REQUEST);
                case 401:
                    throw new RequestException(WFErrorCode.UNAUTHORIZED);
                case 403:
                    throw new RequestException(WFErrorCode.FORBIDDEN);
                case 404:
                    throw new RequestException(WFErrorCode.NOT_FOUND);
                case 405:
                    throw new RequestException(WFErrorCode.METHOD_NOT_ALLOWED);
                case 500:
                    throw new RequestException(WFErrorCode.INTERNAL_SERVER_ERROR);
                case 502:
                    throw new RequestException(WFErrorCode.BAD_GATEWAY);
                case 503:
                    throw new RequestException(WFErrorCode.SERVICE_UNAVAILABLE);
                default:
                    if(response.getStatusCode() / 100 == 4)
                        throw new RequestException(WFErrorCode.UNKNOW_REQUEST_EXCEPTION);
                    else if(response.getStatusCode() / 100 == 5)
                        throw new RequestException(WFErrorCode.UNKNOW_HOST_EXCEPTION);

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
                throw new RequestException(WFErrorCode.SOCKET_EXCEPTION);
            else
                throw new RequestException(WFErrorCode.UNKNOW);
        }
        return null;


    }

    public abstract CloseableHttpClient buildClient();
    public abstract void signature(HttpRequestBase requestBase);

}
