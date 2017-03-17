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

    @Test
    public void testMapToBean(){
        String str = "[1, 2, 3, 4, 5]";
        int[] list = JsonUtils.mapToBean(str, int[].class);
        System.out.println(list[0]);
        Assert.assertEquals(list[0], 1);
    }
}
