import org.apache.http.entity.ContentType;
import org.junit.*;
import org.radrso.plugins.JsonUtils;
import org.radrso.plugins.requests.BaseRequest;
import org.radrso.plugins.requests.RequestFactory;
import org.radrso.plugins.requests.entity.MethodEnum;
import org.radrso.plugins.requests.entity.Response;
import org.radrso.plugins.requests.entity.exceptions.ResponseCode;
import org.radrso.plugins.requests.entity.exceptions.impl.RequestException;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by rao-mengnan on 2017/3/11.
 */
public class TestRequestFactory {
    Map<String, Object> headers = new HashMap<>();

    @Before
    public void before() {
        headers.put("User-Agent",
                "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36");
    }

    @Test
    public void testGet() throws RequestException {
        String url = "http://gc.ditu.aliyun.com/geocoding";
        Map<String, Object> param = new HashMap<>();
        param.put("a", "南京");
        BaseRequest request = RequestFactory.createRequest(url, MethodEnum.GET, headers, param, null, false);
        Response response = request.sendRequest();
        Assert.assertEquals(response.getStatusCode(), 200);
        System.out.println("[DEBUG]---get---" + response.getContent());

        url = "https://api.douban.com/v2/book/1220562";
        request = RequestFactory.createRequest(url, MethodEnum.GET, headers, null, null, false);
        response = request.sendRequest();
        Assert.assertEquals(response.getStatusCode(), 200);
        System.out.println("[DEBUG]---get---" + response.getContent());

        url = "https://api.douban.com/v2/book/search";
        param = new HashMap<>();
        param.put("q", "东野圭吾");
        param.put("start", 1);
        param.put("count", "30");
        param.put("tag", "文字");
        request = RequestFactory.createRequest(url, MethodEnum.GET, headers, JsonUtils.getJsonElement(param), null, true);
        response = request.sendRequest();
        Assert.assertEquals(response.getStatusCode(), 200);
        System.out.println("[DEBUG]---get---" + response.getContent());
        request.closeConnectionPool();

        url = "https://api.douban.com/v2/user/~me";
        headers.put("Authorization", "Bearer a14afef0f66fcffce3e0fcd2e34f6ff4");
        try {
            request = RequestFactory.createRequest(url, MethodEnum.GET, headers, JsonUtils.getJsonElement(param), null, true);
            request.sendRequest();
        } catch (RequestException e) {
            Assert.assertEquals(e.getCode(), ResponseCode.HTTP_BAD_REQUEST);
            System.out.println("[DEBUG]---get---" + e.getMessage());
        }

    }

    @Test
    public void testPost() throws RequestException {
        String url = "http://gw.api.taobao.com/router/rest";
        ContentType type = ContentType.create(ContentType.APPLICATION_FORM_URLENCODED.getMimeType(), Charset.forName("utf-8"));
        Object params = "app_key=12129701&format=json&" +
                "method=taobao.top.secret.register&" +
                "partner_id=apidoc&" +
                "sign=FADA9CA5CEBA6C82C0C949CB3CB5AE7D&" +
                String.format("sign_method=hmac&timestamp=%s&v=2.0", System.currentTimeMillis());

        BaseRequest request = RequestFactory.createRequest(url, MethodEnum.POST, headers, params, type, true);
        Response response = null;
        boolean contains = false;
        for (int i = 0; i < 5; i++) {
            response = request.sendRequest();
            contains = response.getContent().contains("\"code\"");
            if (contains)
                break;
        }
        System.out.println("[DEBUG]---post---" + "http post request, x-www-form: " + response.getContent());
        Assert.assertEquals(contains, true);

        url = "http://www.tuling123.com/openapi/api";
        type = ContentType.APPLICATION_JSON;
        Map<String, Object> body = new HashMap<>();
        body.put("key", "no_api_key");
        body.put("info", "你好");
        body.put("userid", "123456");
        request = RequestFactory.createRequest(url, MethodEnum.POST, headers, JsonUtils.getJsonElement(body), type, true);
        response = request.sendRequest();
        System.out.println("[DEBUG]---post---" + response.getContent());
        Assert.assertEquals(response.getStatusCode(), 200);

    }

    @Ignore
    @Test
    public void testPut() {

    }

    @Ignore
    @Test
    public void testDelete() {

    }
}
