package org.radrso.plugins.requests.impl;

import lombok.Data;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.radrso.plugins.requests.entity.Method;
import org.radrso.plugins.requests.Request;


/**
 * Created by raomengnan on 16-12-10.
 */
@Data
public class HttpRequest extends Request{


    public HttpRequest(String url, Method method, Object params, ContentType contentType, HttpRequestBase request) {
        super(url, method, params, contentType, request);
    }


    @Override
    public CloseableHttpClient buildClient() {
        return HttpClients.createDefault();
    }

    @Override
    public void signature(HttpRequestBase requestBase) {

    }
}
