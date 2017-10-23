package org.radrso.plugins;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Rao-Mengnan
 * on 2017/10/23.
 */
public class ClassUtil {

    private static final String UTILS_CLASS = "Date|List|Map";
    private static final Map<String, Class> CLASS_MAP;

    static {
        CLASS_MAP = new HashMap<>();
        CLASS_MAP.put("int[]", int[].class);
        CLASS_MAP.put("double[]", double[].class);
        CLASS_MAP.put("float[]", float[].class);
        CLASS_MAP.put("short", short[].class);
        CLASS_MAP.put("byte[]", byte[].class);
        CLASS_MAP.put("char[]", char[].class);
        CLASS_MAP.put("Integer[]", Integer[].class);
        CLASS_MAP.put("Double[]", Double[].class);
        CLASS_MAP.put("Float[]", Float[].class);
        CLASS_MAP.put("Short[]", Short[].class);
        CLASS_MAP.put("Byte[]", Byte[].class);
        CLASS_MAP.put("Character[]", Character[].class);
        CLASS_MAP.put("Boolean[]", Boolean[].class);
    }

    public static  <T> T conversion (Object o, Class<T> clazz) {
        return JsonUtils.mapToBean(o.toString(), clazz);
    }

    public static Class objectClass(String type) {
        if (type == null) {
            return String.class;
        }
        type = casting(type);
        try {
            Class clazz;
            if (type.contains("["))
                clazz = CLASS_MAP.get(type);
            else
                clazz = CustomClassLoader.getClassLoader().loadClass(type);
            if (clazz == null)
                throw new ClassNotFoundException(String.format("No such class [%s]", type));
            return clazz;
            // TODO 包装错误
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(String.format("Param type error: (%s)", type) + e);
        } catch (Throwable throwable) {
            throw new RuntimeException("Value case error: " + throwable.getMessage());
        }

    }

    /**
     * Java base type
     *
     * @param type {@link java.lang} {@link java.util.Date}
     * @return {@link java.lang.Number} or {@link java.util.Date}
     */
    public static String casting(String type) {
        if (type.contains(".") || type.contains("["))
            return type;

        type = type.substring(0, 1).toUpperCase() + type.substring(1).toLowerCase();
        if (UTILS_CLASS.contains(type) && !type.contains("|"))
            return "java.util." + type;
        if (type.equals("Int")) {
            type = "Integer";
        }
        return "java.lang." + type;
    }
}
