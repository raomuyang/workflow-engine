package org.radrso.workflow.resolvers;

import org.junit.Assert;
import org.junit.Test;
import org.radrso.workflow.entities.schema.items.Step;
import org.radrso.workflow.entities.info.WorkflowResult;

/**
 * Created by rao-mengnan on 2017/3/12.
 */
public class TestStepResolver {

    public static String sum_1(String str, Integer... args){
        int sum = 0;
        for (int i = 0; i < args.length; i++)
            sum += args[i];
        return str + sum;
    }

    public static String sum_2(String str, int... args){
        int sum = 0;
        for (int i = 0; i < args.length; i++)
            sum += args[i];
        return str + sum;
    }

    @Test
    public void testClassRequest() {
        Step step = new Step();
        step.setCall("class:org.radrso.workflow.resolvers.TestStepResolver");
        step.setMethod("sum_1");
        String[] paramNames = new String[]{"default", "default", "default", "default", "default"};
        Integer[] params = new Integer[]{1, 2, 3, 4, 5};
        RequestResolverImpl resolver = new RequestResolverImpl(step, new Object[]{"test:", params}, paramNames);
        WorkflowResult response = resolver.classRequest();
        System.out.println("Result:" + response);

        step.setMethod("sum_2");
        response = resolver.classRequest();
        Assert.assertNotEquals(response.getCode(), 200);
    }

    @Test
    public void testNetRequest() {
        Step step = new Step();
        step.setCall("http://www.tuling123.com/openapi/{endpoint}");
        step.setMethod("POsT");
        String[] paramNames = new String[]{"key", "info", "userid", "{endpoint}", "$Content-Type"};
        String[] params = new String[]{"asdf", "你好", "12345678", "api", "asdfasf"};

        RequestResolverImpl resolver = new RequestResolverImpl(step, params, paramNames);
        WorkflowResult response = resolver.netRequest();
        System.out.println(response.getBody());
        System.out.println(response.getBody().getClass());
        Assert.assertEquals(step.getCall(), "http://www.tuling123.com/openapi/api");
        Assert.assertEquals(response.getBody().toString().contains("40001"), true);


        step.setCall("https://api.douban.com/v2/user/~me");
        paramNames = new String[]{"$Authorization"};
        params = new String[]{"Bearer a14afef0f66fcffce3e0fcd2e34f6ff4"};
        step.setMethod("get");
        resolver = new RequestResolverImpl(step, params, paramNames);
        response = resolver.netRequest();
        System.out.println(response);
        Assert.assertEquals(response.getMsg().contains("a14afef0f66fcffce3e0fcd2e34f6ff4"), true);


    }
}
