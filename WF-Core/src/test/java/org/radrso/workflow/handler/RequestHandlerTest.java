package org.radrso.workflow.handler;

import org.apache.http.entity.ContentType;
import org.junit.Assert;
import org.junit.Test;
import org.radrso.workflow.constant.WFStatusCode;
import org.radrso.workflow.entities.model.WorkflowResult;
import org.radrso.workflow.entities.schema.items.Step;

import java.util.*;

/**
 * Created by Rao-Mengnan
 * on 2017/10/25.
 */
public class RequestHandlerTest {
    public static String sum_1(String str, Integer... args){
        int sum = 0;
        for (int i = 0; i < args.length; i++)
            sum += args[i];
        return str + ": " + sum;
    }

    public static int sum_2(int a, int b, int c, int d, int e){
        return a + b +c + d + e;
    }

    @Test
    public void classRequest() throws Exception {
        Step step = new Step();
        step.setCall("class:org.radrso.workflow.handler.RequestHandlerTest");
        String[] paramNames = new String[]{"default", "default", "default", "default", "default"};
        Integer[] params = new Integer[]{1, 2, 3, 4, 5};
        List<Map<String, Object>> paramList = initParam(paramNames, params);

        RequestHandler handler = new RequestHandler(step, paramList);
        step.setMethod("sum_1");
        WorkflowResult response = handler.classRequest();
        Assert.assertNotEquals(response.getCode(), WFStatusCode.OK.code());
        System.out.println(response);

        step.setMethod("sum_2");
        response = handler.classRequest();
        Assert.assertEquals(response.getCode(), WFStatusCode.OK.code());
        System.out.println(response);
        Assert.assertEquals(true, response.getBody() instanceof Integer);

        Map<String, Object> map1 = new HashMap<>();
        map1.put("default", "test");
        Map<String, Object> map2 = new HashMap<>();
        Integer[] p = new Integer[] {1, 2, 3};
        map2.put("default", p);
        step.setMethod("sum_1");
        paramList = Arrays.asList(map1, map2);
        handler = new RequestHandler(step, paramList);
        response = handler.classRequest();
        System.out.println(response);
        Assert.assertEquals(response.getCode(), WFStatusCode.OK.code());
    }

    @Test
    public void netRequest() throws Exception {
        String API = "http://www.tuling123.com/openapi/api";
        Step step = new Step();
        step.setCall("http://www.tuling123.com/openapi/{endpoint}");
        step.setMethod("POsT");
        String[] paramNames = new String[]{"key", "model", "userid", "{endpoint}", "$Content-Type"};
        String[] params = new String[]{"asdf", "你好", "12345678", "api", "application/json;gbk"};
        List<Map<String, Object>> paramList = initParam(paramNames, params);

        RequestHandler handler = new RequestHandler(step, paramList);
        handler.initRequestInfo();
        Assert.assertEquals(API, step.getCall());

        ContentType contentType = ContentType.create("application/json", "gbk");
        Assert.assertEquals(contentType.getCharset(), handler.contentType.getCharset());
        Assert.assertEquals(contentType.getMimeType(), handler.contentType.getMimeType());

        Assert.assertEquals(1, handler.httpHeaders.size());
        Assert.assertEquals("asdf", handler.httpParamMap.get("key"));
        System.out.println(handler.httpHeaders);
        System.out.println(handler.httpParamMap);
    }

    @Test
    public void netRequest2() {
        Step step = new Step();
        step.setCall("https://api.douban.com/v2/user/me");
        step.setMethod("get");

        String[] paramNames = new String[]{"$Authorization"};
        String[] params = new String[]{"Bearer a14afef0f66fcffce3e0fcd2e34f6ff4"};
        List<Map<String, Object>> paramList = initParam(paramNames, params);

        RequestHandler handler = new RequestHandler(step, paramList);
        handler.initRequestInfo();

        Assert.assertEquals(params[0], handler.httpHeaders.get("Authorization"));
        WorkflowResult result = handler.netRequest();
        Assert.assertNotEquals(WFStatusCode.OK.code(), result.getCode());
    }

    private List<Map<String, Object>> initParam(String[] paramNames, Object[] params) {
        List<Map<String, Object>> paramList = new ArrayList<>();
        for (int i = 0; i < params.length; i++) {
            Map<String, Object> map = new HashMap();
            map.put(paramNames[i], params[i]);
            paramList.add(map);
        }
        return paramList;
    }

}