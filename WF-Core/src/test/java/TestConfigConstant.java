import org.junit.Assert;
import org.junit.Test;
import org.radrso.workflow.constant.ConfigConstant;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rao-mengnan on 2017/3/12.
 */
public class TestConfigConstant {

    @Test
    public void testMatcher() {
        String str = "{asdf}{ghjk}{lmnb}";
        String[] matchers0 = ConfigConstant.matcherValuesEscape(str);
        Assert.assertEquals(matchers0[1], "{ghjk}");

        String[] matchers1 = ConfigConstant.matcherValuesEscape(str, 1);
        Assert.assertEquals(matchers1[2], "lmnb");
    }
    @Test
    public void test(){
        List<String> t = new ArrayList();
        t.add("131");
        t.add("3123");
        String[] os = new String[]{};
        t.toArray(os);
        System.out.println(os.length);
    }
}
