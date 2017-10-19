package org.radrso.workflow.constant;

import org.junit.Assert;
import org.junit.Test;
import org.radrso.workflow.constant.EngineConstant;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rao-mengnan on 2017/3/12.
 */
public class TestEngineConstant {

    @Test
    public void testMatcher() {
        String str = "{asdf}{ghjk}{lmnb}";
        String[] matchers0 = EngineConstant.matcherValuesEscape(str);
        Assert.assertEquals(matchers0[1], "{ghjk}");

        String[] matchers1 = EngineConstant.matcherValuesEscape(str, 1);
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
