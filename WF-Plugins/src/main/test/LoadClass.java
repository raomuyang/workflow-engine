import java.lang.reflect.InvocationTargetException;

/**
 * Created by raomengnan on 17-1-9.
 */
public class LoadClass {
    public static void main(String[] args) throws ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        Class clazz = Class.forName("SystemDetails");
        Object o = clazz.newInstance();
        clazz.getMethod("outputDetails").invoke(o);
    }
}
