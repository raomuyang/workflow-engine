import com.google.gson.JsonElement;
import org.junit.Assert;
import org.junit.Test;
import org.radrso.plugins.JsonUtils;

/**
 * Created by rao-mengnan on 2017/3/16.
 */
public class TestJsonUtils {

    @Test
    public void testGetElements() {
        int[] a = {1, 2, 3, 4, 5, 6};
        JsonElement element = JsonUtils.getJsonElement(a);
        System.out.println(element);
        Assert.assertEquals(element.getAsJsonArray().get(0).getAsString(), "1");
    }
}
