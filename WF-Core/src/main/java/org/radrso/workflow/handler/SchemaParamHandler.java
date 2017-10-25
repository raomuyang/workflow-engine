package org.radrso.workflow.handler;

import lombok.extern.log4j.Log4j;
import org.radrso.plugins.ClassUtil;
import org.radrso.workflow.constant.EngineConstant;
import org.radrso.workflow.constant.WFStatusCode;
import org.radrso.workflow.entities.exceptions.SchemaResolveException;
import org.radrso.workflow.entities.exceptions.WFError;
import org.radrso.workflow.entities.model.WorkflowResult;
import org.radrso.workflow.internal.model.WorkflowInstanceInfo;
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

    private WorkflowInstanceInfo workflowInstanceInfo;

    public SchemaParamHandler(WorkflowInstanceInfo workflowInstanceInfo) {
        this.workflowInstanceInfo = workflowInstanceInfo;
    }

    public List<Map<String, Object>> parameters(Transfer transfer) throws SchemaResolveException {
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
     * @param param {output}.step_x_sign.xxx.xxx
     * @return object
     */
    public Object convertStrParam(String param, String type) throws SchemaResolveException {

        if (type == null || type.equals("")) type = "String";
        if (EngineConstant.SCHEMA_INSTANCE_ID_VALUE.toLowerCase()
                .equals(String.valueOf(param).toLowerCase()))
            return workflowInstanceInfo.getInstanceId();

        try {
            if (param.startsWith(EngineConstant.OUTPUT_VALUE)) {

                String[] index = param.replace(EngineConstant.OUTPUT_VALUE + ".", "").split("\\.");
                WorkflowResult workflowResult = getStepResultBody(index[0]);

                if (index.length == 1) {
                    log.debug(String.format("Request index: %s", index[0]));
                    return workflowResult.getBody();
                } else {
                    Object result = workflowResult.getBody();
                    Map kv = ClassUtil.conversion(result, Map.class);
                    for (int i = 1; i < index.length; ++i) {
                        result = kv.get(index[i]);
                        if (i + 1 <= index.length) kv = ClassUtil.conversion(result, Map.class);
                    }
                    return ClassUtil.conversion(result, ClassUtil.objectClass(type));
                }
            }

            return ClassUtil.conversion(param, ClassUtil.objectClass(type));
        } catch (ClassNotFoundException e) {
            throw new SchemaResolveException(WFStatusCode.CLASS_NOT_FOUND.code(), e.getMessage());
        } catch (Throwable e) {
            if (e instanceof WFError) {
                WFError e1 = (WFError) e;
                throw new SchemaResolveException(e1.getCode(), e1.getDetailMessage(), e);
            }
            throw new SchemaResolveException("Invalid schema: " + e.getMessage(), e);
        }
    }


    private WorkflowResult getStepResultBody(String stepSign) {
        return workflowInstanceInfo.getStepProgressMap().get(stepSign).getResult();
    }

}
