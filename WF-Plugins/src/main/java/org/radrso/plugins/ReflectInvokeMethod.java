package org.radrso.plugins;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by raomengnan on 17-1-10.
 */
public class ReflectInvokeMethod {

    private ReflectInvokeMethod() {
    }

    public static Object invoke(Class clazz, Object instance, String methodName) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Object ret = clazz.getMethod(methodName).invoke(instance);
        return ret;
    }

    public static Object invoke(Class clazz, Object instance, String methodName, Object... args) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        Class[] argClasses1 = new Class[]{};
        Class[] argClasses2 = new Class[]{};
        if (args != null) {
            argClasses1 = new Class[args.length];
            argClasses2 = new Class[args.length];

            for (int i = 0; i < args.length; i++) {
                Class cla = args[i].getClass();
                argClasses1[i] = cla;
                argClasses2[i] = cla;

                if (Number.class.isAssignableFrom(cla)) {
                    String name = cla.getSimpleName();
                    switch (name) {
                        case "Integer":
                            argClasses2[i] = int.class;
                            break;
                        case "Float":
                            argClasses2[i] = float.class;
                            break;
                        case "Double":
                            argClasses2[i] = double.class;
                            break;
                        case "Short":
                            argClasses2[i] = short.class;
                    }
                } else if ("Boolean".equals(cla.getSimpleName()))
                    argClasses2[i] = boolean.class;
            }
        }

        try {
            Method m = clazz.getMethod(methodName, argClasses1);
            return m.invoke(instance, args);
        } catch (NoSuchMethodException e) {
            Method m = clazz.getMethod(methodName, argClasses2);
            return m.invoke(instance, args);
        }
    }


}
