import org.junit.Assert;
import org.junit.Test;
import org.radrso.workflow.StandardString;

/**
 * Created by rao-mengnan on 2017/3/12.
 */
public class TestStandardString {

    @Test
    public void testMatcher(){
        String str = "{asdf}{ghjk}{lmnb}";
        String[] matchers0 = StandardString.matcherValuesEscape(str);
        Assert.assertEquals(matchers0[1], "{ghjk}");

        String[] matchers1 = StandardString.matcherValuesEscape(str, 1);
        Assert.assertEquals(matchers1[2], "lmnb");
    }
}
