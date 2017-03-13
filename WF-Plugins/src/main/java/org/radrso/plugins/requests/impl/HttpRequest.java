package org.radrso.plugins.requests.impl;

import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.radrso.plugins.requests.entity.MethodEnum;
import org.radrso.plugins.requests.BaseRequest;

import java.util.Map;


/**
 * Created by raomengnan on 16-12-10.
 */

public class HttpRequest extends BaseRequest {
    private static PoolingHttpClientConnectionManager poolManager;
    private final static int MAX_TOTAL_POOL = 200;

    public HttpRequest(String url, MethodEnum method, Map<String, Object> headers,
                       Object params, ContentType contentType, Boolean usePool) throws ReflectiveOperationException  {
        super(url, method, headers,params, contentType, usePool);
    }

    @Override
    public CloseableHttpClient buildClient() {
        return HttpClients.createMinimal();
    }

    @Override
    public CloseableHttpClient buildClientUsePool() {
        if(poolManager == null) {
            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("http", PlainConnectionSocketFactory.getSocketFactory())
                    .build();

            poolManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
            poolManager.setMaxTotal(MAX_TOTAL_POOL);
        }

        return HttpClients.custom().setConnectionManager(poolManager).setConnectionManagerShared(true).build();
    }

    @Override
    public void signature(HttpRequestBase requestBase) {

    }

    @Override
    public void closeConnectionPool() {
        try {
            poolManager.close();
            poolManager = null;
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
