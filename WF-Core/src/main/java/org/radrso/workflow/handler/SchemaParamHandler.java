package org.radrso.workflow.handler;

import lombok.extern.log4j.Log4j;
import org.radrso.plugins.CustomClassLoader;
import org.radrso.plugins.JsonUtils;
import org.radrso.workflow.constant.EngineConstant;
import org.radrso.workflow.entities.exceptions.ConfigReadException;
import org.radrso.workflow.entities.exceptions.UnknownExceptionInRunning;
import org.radrso.workflow.entities.info.WorkflowInstance2;
import org.radrso.workflow.entities.schema.items.InputItem;
import org.radrso.workflow.entities.schema.items.Transfer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by rao-mengnan on 2017/3/16.
 * Base param convert.
 */
@Log4j
public class SchemaParamHandler {
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


    private WorkflowInstance2 workflowInstance;

    public SchemaParamHandler(WorkflowInstance2 workflowInstance) {
        this.workflowInstance = workflowInstance;
    }

    public List<Map<String, Object>> parameters(Transfer transfer) throws ConfigReadException, UnknownExceptionInRunning {
        List<InputItem> inputs = transfer.getInput();
        if (inputs == null || inputs.size() == 0)
            return new ArrayList<>();

        List<Map<String, Object>> paramList = new ArrayList<>();

        for (InputItem inputItem : inputs) {
            Map<String, Object> kv = new HashMap<>();
            String type = inputItem.getType();
            Object value = convertStrParam(inputItem.getValue(), type);
            kv.put(inputItem.getName(), value);
            paramList.add(kv);
        }

        return paramList;
    }

    /**
     *
     * @param paramStr {output}.step_x_sign.xxx.xxx
     * @return object
     */
    public Object convertStrParam(String paramStr, String type) throws ConfigReadException {

        if (EngineConstant.SCHEMA_INSTANCE_ID_VALUE.toLowerCase()
                .equals(String.valueOf(paramStr).toLowerCase()))
            return workflowInstance.getInstanceId();

        try {
            if (paramStr.startsWith(EngineConstant.OUTPUT_VALUE)) {

                String[] index = paramStr.replace(EngineConstant.OUTPUT_VALUE + ".", "").split("\\.");
                if (index.length == 1) {
                    log.debug(String.format("Request index: %s", index[0]));
                    return getStepResultBody(index[0]);
                } else {
                    Object result = getStepResultBody(index[0]);
                    Map kv = conversion(result, Map.class);
                    for (int i = 1; i < index.length; ++i) {
                        result = kv.get(index[i]);
                        if (i + 1 < index.length) kv = conversion(result, Map.class);
                    }
                    return conversion(result, objectClass(type));
                }
            }
        } catch (Exception e) {
            throw new ConfigReadException("Invalid config.", e);
        }

        return paramStr;
    }

    public <T> T conversion (Object o, Class<T> clazz) {
        return JsonUtils.mapToBean(o.toString(), clazz);
    }

    public Class objectClass(String type) {
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
    private String casting(String type) {
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

    private Object getStepResultBody(String stepSign) {
        return workflowInstance.getStepProcessMap().get(stepSign).getResult();
    }

}
