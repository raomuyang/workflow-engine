package org.radrso.plugins.requests.impl;

import lombok.Data;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContexts;
import org.radrso.plugins.requests.BaseRequest;
import org.radrso.plugins.requests.entity.MethodEnum;

import javax.net.ssl.SSLContext;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;

/**
 * Created by raomengnan on 16-12-10.
 */
public class HttpsRequest extends BaseRequest {

    public HttpsRequest(String url, MethodEnum method, Map<String, Object> headers,
                        Object params, ContentType contentType, Boolean usePool) throws ReflectiveOperationException  {
        super(url, method, headers,params, contentType, usePool);
    }

    @Override
    public CloseableHttpClient buildClient() {
        try {
            return new CustomSSLSocketFactory().create();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public CloseableHttpClient buildClientUsePool() {
        try {
            return new CustomSSLSocketFactory().createFromPool();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void signature(HttpRequestBase requestBase) {

    }

    @Override
    public void closeConnectionPool() {
        try {
            CustomSSLSocketFactory.poolManager.close();
            CustomSSLSocketFactory.poolManager = null;
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Data
    private static class CustomSSLSocketFactory {
        private SSLContext sslContext;
        private SSLConnectionSocketFactory ssf;
        private final static int MAX_TOTAL_POOL = 200;
        private volatile static PoolingHttpClientConnectionManager poolManager;

        public CustomSSLSocketFactory() throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
            sslContext = SSLContexts.custom().loadTrustMaterial(null, new TrustStrategy() {
                @Override
                public boolean isTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                    return true;
                }
            }).build();

            ssf = new SSLConnectionSocketFactory(
                    this.sslContext);
        }

        public CloseableHttpClient create(){
            return HttpClients.custom().setSSLSocketFactory(ssf).build();
        }

        public CloseableHttpClient createFromPool(){
            if(poolManager == null) {
                Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                        .register("http", PlainConnectionSocketFactory.getSocketFactory())
                        .register("https", ssf)
                        .build();
                poolManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
                poolManager.setMaxTotal(MAX_TOTAL_POOL);
            }

            return HttpClients.custom().setConnectionManager(poolManager).setConnectionManagerShared(true).build();
        }

    }
}
