package org.radrso.workflow.handler;

import lombok.extern.log4j.Log4j;
import org.radrso.plugins.ClassUtil;
import org.radrso.plugins.CustomClassLoader;
import org.radrso.plugins.JsonUtils;
import org.radrso.workflow.constant.EngineConstant;
import org.radrso.workflow.entities.exceptions.ConfigReadException;
import org.radrso.workflow.entities.exceptions.UnknownExceptionInRunning;
import org.radrso.workflow.entities.model.WorkflowInstance2;
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



    private WorkflowInstance2 workflowInstance;

    public SchemaParamHandler(WorkflowInstance2 workflowInstance) {
        this.workflowInstance = workflowInstance;
    }

    public List<Map<String, Object>> parameters(Transfer transfer) throws Exception {
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
     * @param param {output}.step_x_sign.xxx.xxx
     * @return object
     */
    public Object convertStrParam(String param, String type) throws ConfigReadException {

        if (EngineConstant.SCHEMA_INSTANCE_ID_VALUE.toLowerCase()
                .equals(String.valueOf(param).toLowerCase()))
            return workflowInstance.getInstanceId();

        try {
            if (param.startsWith(EngineConstant.OUTPUT_VALUE)) {

                String[] index = param.replace(EngineConstant.OUTPUT_VALUE + ".", "").split("\\.");
                if (index.length == 1) {
                    log.debug(String.format("Request index: %s", index[0]));
                    return getStepResultBody(index[0]);
                } else {
                    Object result = getStepResultBody(index[0]);
                    Map kv = ClassUtil.conversion(result, Map.class);
                    for (int i = 1; i < index.length; ++i) {
                        result = kv.get(index[i]);
                        if (i + 1 < index.length) kv = ClassUtil.conversion(result, Map.class);
                    }
                    return ClassUtil.conversion(result, ClassUtil.objectClass(type));
                }
            }
        } catch (Exception e) {
            throw new ConfigReadException("Invalid config.", e);
        }

        return param;
    }


    private Object getStepResultBody(String stepSign) {
        return workflowInstance.getStepProcessMap().get(stepSign).getResult();
    }

}
