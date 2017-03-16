package org.radrso.plugins;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by raomengnan on 17-1-10.
 */
public class ReflectInvokeMethod {

    private ReflectInvokeMethod() {
    }

    /**
     * 调用无参方法
     * @param clazz 目标类
     * @param instance  目标类对象
     * @param methodName    调用方法名
     * @return  返回调用结果
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public static Object invoke(Class clazz, Object instance, String methodName) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Object ret = clazz.getMethod(methodName).invoke(instance);
        return ret;
    }

    /**
     *
     * @param clazz 目标类
     * @param instance  目标类对象
     * @param methodName    调用方法名
     * @param args  <p>目标方法的参数，当目标方法的参数有多个时，必须以 new Object[]{param1, param2, param3...}的形式传入</>
     *              <p>注意目标方法的参数为基本类型的数组或可变长参数时，务必不要与包装类型数组混用，</>
     *              <p>否则会抛出NoSuchMethodException的异常</>
     * @return  方法调用结果
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws NoSuchMethodException
     */
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
