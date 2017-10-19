package org.radrso.workflow.internal.resolver;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import lombok.extern.log4j.Log4j;
import org.radrso.plugins.CustomClassLoader;
import org.radrso.plugins.JsonUtils;
import org.radrso.plugins.StringUtils;
import org.radrso.workflow.constant.EngineConstant;
import org.radrso.workflow.entities.schema.items.InputItem;
import org.radrso.workflow.entities.schema.items.Transfer;
import org.radrso.workflow.entities.exceptions.ConfigReadException;
import org.radrso.workflow.entities.exceptions.UnknownExceptionInRunning;
import org.radrso.workflow.entities.info.StepStatus;
import org.radrso.workflow.entities.info.WorkflowInstance;
import org.radrso.workflow.resolvers.ParamsResolver;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by rao-mengnan on 2017/3/16.
 */
@Log4j
public class ParamsResolverImpl implements ParamsResolver {
    private static final String UTILS_CLASS = "Data|List|Map";
    private static final Map<String, Class> classMap;

    static {
        classMap = new HashMap<>();
        classMap.put("int[]", int[].class);
        classMap.put("double[]", double[].class);
        classMap.put("float[]", float[].class);
        classMap.put("short", short[].class);
        classMap.put("byte[]", byte[].class);
        classMap.put("char[]", char[].class);
        classMap.put("Integer[]", Integer[].class);
        classMap.put("Double[]", Double[].class);
        classMap.put("Float[]", Float[].class);
        classMap.put("Short[]", Short[].class);
        classMap.put("Byte[]", Byte[].class);
        classMap.put("Character[]", Character[].class);
        classMap.put("Boolean[]", Boolean[].class);
    }

    private WorkflowInstance workflowInstance;

    public ParamsResolverImpl(WorkflowInstance workflowInstance) {
        this.workflowInstance = workflowInstance;
    }

    @Override
    public Object[] resolverTransferParams(Transfer transfer) throws ConfigReadException, UnknownExceptionInRunning {
        List<InputItem> inputs = transfer.getInput();
        if (inputs == null || inputs.size() == 0)
            return new Object[]{};

        Object[] params = new Object[inputs.size()];
        String[] paramsNames = new String[inputs.size()];
        for (int i = 0; i < inputs.size(); i++) {
            InputItem inputItem = inputs.get(i);
            Object value = resolverStringToParams(inputItem.getValue());
            String type = inputItem.getType();

            value = parseValue(type, value);
            params[i] = value;
            paramsNames[i] = inputItem.getName();
        }

        getStepStatusMap().get(transfer.getTo()).setParams(params);
        getStepStatusMap().get(transfer.getTo()).setParamNames(paramsNames);
        return params;
    }

    @Override
    public Object resolverStringToParams(String paramStr) throws UnknownExceptionInRunning, ConfigReadException {
        String errorMsg = null;

        if (paramStr.toLowerCase().equals(EngineConstant.SCHEMA_INSTANCE_ID_VALUE))
            return workflowInstance.getInstanceId();

        if (paramStr.startsWith(EngineConstant.OUTPUT_VALUE)) {

            int parseStrIndex = 2;
            String[] sp = null;
            Object result = null;
            try {
                // 去除所有的中括号[],分割
                paramStr = paramStr.replaceAll("\\[", ".").replaceAll("]", "");
                sp = paramStr.split("\\.");
                // sp[1]是step-sign，即该step的id。初始时result为参数表达式的step返回值。WFResponse中的body才是返回值实体
                Object output = getStepStatusMap().get(sp[1]).getWfResponse().getBody();
                result = output;

                for (; parseStrIndex < sp.length; ++parseStrIndex) {
                    if (sp.length == parseStrIndex) {
                        return result;
                    }

                    Class c = result.getClass();
                    if (String.class.isAssignableFrom(c)) {
                        result = JsonUtils.getJsonElement(result);

                    }

                    JsonElement element = JsonUtils.getJsonElement(result);
                    // 判断是否为Json数组
                    if (StringUtils.isInteger(sp[parseStrIndex])) {
                        JsonArray array = element.getAsJsonArray();
                        result = array.get(Integer.valueOf(sp[parseStrIndex]));
                    } else {
                        result = element.getAsJsonObject().get(sp[parseStrIndex]);
                    }
                }

            } catch (IndexOutOfBoundsException e1) {
                log.error(e1);
                errorMsg = "Read Config Error:" + e1;
            } catch (NullPointerException e2) {
                errorMsg = "No such value: " + paramStr + "/" + sp[parseStrIndex];
            } catch (Throwable e3) {
                log.debug(e3);
                throw new UnknownExceptionInRunning("Exception in resolve param [%s]".format(paramStr) + "/case:" + e3, e3);
            }

            if (errorMsg != null)
                throw new ConfigReadException(errorMsg);
            return result;
        }
        return paramStr;
    }

    @Override
    public Object parseValue(String type, Object o) throws ConfigReadException {
        if (type == null)
            return o.toString();
        try {
            type = casePrimitiveType(type);
            Class clazz;
            if (type.contains("["))
                clazz = classMap.get(type);
            else
                clazz = CustomClassLoader.getClassLoader().loadClass(type);
            if (clazz == null)
                throw new ClassNotFoundException(String.format("No such class [%s]", type));
            return JsonUtils.mapToBean(o.toString(), clazz);
        } catch (ClassNotFoundException e) {
            throw new ConfigReadException(String.format("Param type error: (%s)", type) + e);
        } catch (Throwable throwable) {
            throw new ConfigReadException("Value case error: " + throwable.getMessage());
        }

    }

    private String casePrimitiveType(String type) {
        if (type.contains(".") || type.contains("["))
            return type;

        type = type.substring(0, 1).toUpperCase() + type.substring(1).toLowerCase();
        if (UTILS_CLASS.contains(type) && !type.contains("|"))
            return "java.util." + type;
        if (type.equals("Int"))
            type = "Integer";
        return "java.lang." + type;
    }

    private Map<String, StepStatus> getStepStatusMap() {
        return this.workflowInstance.getStepStatusesMap();
    }

}
