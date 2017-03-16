import org.junit.Assert;
import org.junit.Test;
import org.radrso.plugins.ReflectInvokeMethod;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by rao-mengnan on 2017/3/15.
 * 测试反射调用方法的工具
 */
public class TestReflectInvodeMethod {

    public static String buildStr(String a, int b) {
        return a + b;
    }

    public int sum_1(int[] args) {
        int sum = 0;
        for (int i = 0; i < args.length; i++)
            sum += args[i];
        return sum;
    }

    public int sum_2(int... args) {
        int sum = 0;
        for (int i = 0; i < args.length; i++)
            sum += args[i];
        return sum;
    }

    @Test
    public void testPackageType() throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException {
        String a = "aaa";
        Integer b = new Integer(123);
        Object ret = ReflectInvokeMethod.invoke(TestReflectInvodeMethod.class,
                null, "buildStr",
                new Object[]{a, b});

        Assert.assertEquals(ret, "aaa123");
    }

    @Test
    public void testInvokeListParam() throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException {
        int[] params_1 = new int[]{1, 2, 3, 4, 5, 6, 7};
        Object ret = ReflectInvokeMethod.invoke(TestReflectInvodeMethod.class,
                TestReflectInvodeMethod.class.newInstance(), "sum_1", params_1);

        System.out.println(ret);
        Assert.assertEquals(ret, new Integer(28));
    }

    @Test
    public void testInvokeMutableParams() throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException {
        int[] params_1 = new int[]{1, 2, 3, 4, 5, 6, 7};
        Object ret = ReflectInvokeMethod.invoke(TestReflectInvodeMethod.class,
                TestReflectInvodeMethod.class.newInstance(), "sum_2", params_1);

        System.out.println(ret);
        Assert.assertEquals(ret, new Integer(28));
    }
}
