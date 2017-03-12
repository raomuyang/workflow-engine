import org.junit.Assert;
import org.junit.Test;
import org.radrso.workflow.entities.config.items.Step;
import org.radrso.workflow.entities.response.WFResponse;
import org.radrso.workflow.resolvers.StepExecuteResolver;

/**
 * Created by rao-mengnan on 2017/3/12.
 */
public class TestStepResolver {
    @Test
    public void testClassRequest(){}

    @Test
    public void testNetRequest(){
        Step step = new Step();
        step.setCall("http://www.tuling123.com/openapi/{endpoint}");
        step.setMethod("POsT");
        String[] paramNames = new String[]{"key", "info", "userid", "{endpoint}", "$Content-Type"};
        String[] params = new String[]{"asdf", "你好", "12345678", "api", "asdfasf"};

        StepExecuteResolver resolver = new StepExecuteResolver(step, params, paramNames);
        WFResponse response = resolver.netRequest();
        System.out.println(response.getResponse());
        Assert.assertEquals(step.getCall(), "http://www.tuling123.com/openapi/api");
        Assert.assertEquals(response.getResponse().toString().contains("40001"), true);


        step.setCall("https://api.douban.com/v2/user/~me");
        paramNames = new String[]{"$Authorization"};
        params = new String[]{"Bearer a14afef0f66fcffce3e0fcd2e34f6ff4"};
        step.setMethod("get");
        resolver = new StepExecuteResolver(step, params, paramNames);
        response = resolver.netRequest();
        System.out.println(response);
        Assert.assertEquals(response.getMsg().contains("a14afef0f66fcffce3e0fcd2e34f6ff4"), true);
    }
}
