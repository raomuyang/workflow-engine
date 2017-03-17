import org.junit.Assert;
import org.junit.Test;
import org.radrso.plugins.ReflectInvokeMethod;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

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

    public int sum_2(Integer... args) {
        int sum = 0;
        for (int i = 0; i < args.length; i++)
            sum += args[i];
        return sum;
    }

    public int sum_3(int... args) {
        int sum = 0;
        for (int i = 0; i < args.length; i++)
            sum += args[i];
        return sum;
    }

    public int sum_4(String str, int... args) {
        int sum = 0;
        for (int i = 0; i < args.length; i++)
            sum += args[i];
        return sum;
    }

    public void list(List args){}

    @Test
    public void testPackageType() throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException {
        String a = "aaa";
        Integer b = new Integer(123);
        Object[] params = new Object[]{a, b};
        Object ret = ReflectInvokeMethod.invoke(TestReflectInvodeMethod.class,
                null, "buildStr",
                params);

        Assert.assertEquals(ret, "aaa123");
    }

    @Test(expected = NoSuchMethodException.class)
    public void testInvokeListParam() throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException {
        int[] params_1 = new int[]{1, 2, 3, 4, 5, 6, 7};
        Object ret = ReflectInvokeMethod.invoke(TestReflectInvodeMethod.class,
                TestReflectInvodeMethod.class.newInstance(), "sum_1", new Object[]{params_1});

        System.out.println("Sum_1:" + ret);
        Assert.assertEquals(ret, new Integer(28));

        System.out.println("testInvokeListParam: will except");
        Integer[] params_2 = new Integer[]{1, 2, 3, 4, 5, 6, 7};
        ReflectInvokeMethod.invoke(TestReflectInvodeMethod.class,
                TestReflectInvodeMethod.class.newInstance(), "sum_1", new Object[]{params_2});


    }

    @Test(expected = NoSuchMethodException.class)
    public void testInvokeMutableParams() throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException {

        Integer[] params_1 = new Integer[]{1, 2, 3, 4, 5, 6, 7};
        Object ret = ReflectInvokeMethod.invoke(TestReflectInvodeMethod.class,
                TestReflectInvodeMethod.class.newInstance(), "sum_2", new Object[]{params_1});

        System.out.println("sum_2" + ret);
        Assert.assertEquals(ret, new Integer(28));

        System.out.println("Will except");
        int[] params_2 = new int[]{1, 2, 3, 4, 5, 6, 7};
        ReflectInvokeMethod.invoke(TestReflectInvodeMethod.class,
                TestReflectInvodeMethod.class.newInstance(), "sum_2", new Object[]{params_2});

    }

    @Test
    public void testInvokeMutableUnpackParams() throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException {
        int[] params_1 = new int[]{1, 2, 3, 4, 5, 6, 7};
        Object ret = ReflectInvokeMethod.invoke(TestReflectInvodeMethod.class,
                TestReflectInvodeMethod.class.newInstance(),
                "sum_3",
                new Object[]{params_1});

        System.out.println("sum_3:" + ret);
        Assert.assertEquals(ret, new Integer(28));

        ret = ReflectInvokeMethod.invoke(TestReflectInvodeMethod.class,
                TestReflectInvodeMethod.class.newInstance(),
                "sum_4",
                new Object[]{"test:", params_1});
        System.out.println("sum_4:" + ret);
    }

    @Test
    public void testList() throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException {
        Object list = new ArrayList();
        Object ret = ReflectInvokeMethod.invoke(TestReflectInvodeMethod.class,
                TestReflectInvodeMethod.class.newInstance(),
                "list",
                new Object[]{list});

    }
}
