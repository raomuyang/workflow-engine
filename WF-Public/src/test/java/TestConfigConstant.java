import org.junit.Assert;
import org.junit.Test;
import org.radrso.workflow.ConfigConstant;

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
}
