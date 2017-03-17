package org.radrso.workflow.resolvers.impl;

import lombok.extern.log4j.Log4j;
import org.radrso.plugins.CustomClassLoader;
import org.radrso.plugins.JsonUtils;
import org.radrso.workflow.ConfigConstant;
import org.radrso.workflow.entities.config.items.InputItem;
import org.radrso.workflow.entities.config.items.Transfer;
import org.radrso.workflow.entities.exceptions.ConfigReadException;
import org.radrso.workflow.entities.exceptions.UnknowExceptionInRunning;
import org.radrso.workflow.entities.wf.StepStatus;
import org.radrso.workflow.entities.wf.WorkflowInstance;
import org.radrso.workflow.resolvers.BaseParamsResolver;

import java.util.List;
import java.util.Map;

/**
 * Created by rao-mengnan on 2017/3/16.
 */
@Log4j
public class ParamsResolver implements BaseParamsResolver {

    private WorkflowInstance workflowInstance;

    public ParamsResolver(WorkflowInstance workflowInstance){
        this.workflowInstance = workflowInstance;
    }

    @Override
    public Object[] resolverTransferParams(Transfer transfer) throws ConfigReadException, UnknowExceptionInRunning {
        List<InputItem> inputs = transfer.getInput();
        if(inputs == null || inputs.size() ==0)
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
    public Object resolverStringToParams(String paramStr) throws UnknowExceptionInRunning, ConfigReadException {
        String errorMsg = null;

        if(paramStr.toLowerCase().equals(ConfigConstant.CONF_INSTANCE_ID_VALUE))
            return workflowInstance.getInstanceId();

        if(paramStr.startsWith(ConfigConstant.OUTPUT_VALUE)){

            int parseStrIndex = 2;
            String[] sp = null;
            Object result = null;
            try {
                // 去除所有的中括号[],分割
                paramStr = paramStr.replaceAll("\\[", ".").replaceAll("]", "");
                sp = paramStr.split("\\.");
                // sp[1]是step-sign，即该step的id。初始时result为参数表达式的step返回值。WFResponse中的response才是返回值实体
                Object output = getStepStatusMap().get(sp[1]).getWfResponse().getResponse();
                result = output;
                for (; parseStrIndex < sp.length; ++parseStrIndex) {
                    Class c = result.getClass();
                    if("java.lang".equals(c.getName().substring(0, c.getName().lastIndexOf("."))))
                        return result;
                    else
                        result = JsonUtils.getJsonElement(result).getAsJsonObject().get(sp[parseStrIndex]);
                }

            }catch (IndexOutOfBoundsException e1){
                log.error(e1);
                errorMsg = "Read Config Error:" + e1;
            } catch (NullPointerException e2){
                errorMsg = "No such value: " + paramStr + "/" + sp[parseStrIndex];
            } catch (Throwable e3){
                log.debug(e3);
                throw new UnknowExceptionInRunning("Exception in resolve param [%s]".format(paramStr) + "/case:" + e3 , e3);
            }

            if(errorMsg != null)
                throw new ConfigReadException(errorMsg);
            return result;
        }
        return paramStr;
    }

    @Override
    public Object parseValue(String type, Object o) throws ConfigReadException {
        if(type == null)
            return o.toString();
        try {
            type = casePrimitiveType(type);
            Class clazz = CustomClassLoader.getClassLoader().loadClass(type);
            return JsonUtils.mapToBean(o.toString(), clazz);
        } catch (ClassNotFoundException e) {
            throw new ConfigReadException(String.format("Param type error: (%s)", type) + e);
        } catch (Throwable throwable){
            throw new ConfigReadException("Value case error: " + throwable);
        }

    }

    private String casePrimitiveType(String type){
        if(type.contains("."))
            return type;

        type = type.substring(0,1).toUpperCase() + type.substring(1).toLowerCase();
        if("Date".equals(type))
            return "java.util." + type;
        if(type.equals("Int"))
            type = "Integer";
        return "java.lang." + type;
    }

    private Map<String, StepStatus> getStepStatusMap(){
        return this.workflowInstance.getStepStatusesMap();
    }

}
