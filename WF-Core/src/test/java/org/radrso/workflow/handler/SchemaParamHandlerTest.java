package org.radrso.workflow.handler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.Before;
import org.junit.Test;
import org.radrso.plugins.JsonUtils;
import org.radrso.workflow.entities.model.StepProcess;
import org.radrso.workflow.entities.model.WorkflowResult;
import org.radrso.workflow.entities.schema.WorkflowSchema;
import org.radrso.workflow.entities.schema.items.Transfer;
import org.radrso.workflow.internal.model.WorkflowInstanceInfo;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Created by Rao-Mengnan
 * on 2017/10/25.
 */
public class SchemaParamHandlerTest {
    private static final String INSTANCE_ID = "test-instance";
    private static final String STEP_SIGN = "step-1";
    private WorkflowSchema schema;
    private WorkflowInstanceInfo instanceInfo;
    private SchemaParamHandler handler;
    @Before
    public void before() {
        String filePath = getClass().getResource("/test-schema.json").getPath();
        schema = JsonUtils.loadJsonFile(filePath, WorkflowSchema.class);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        System.out.println(gson.toJson(schema));

        instanceInfo = new WorkflowInstanceInfo();
        instanceInfo.setInstanceId(INSTANCE_ID);
        StepProcess process = new StepProcess(instanceInfo.getInstanceId(), STEP_SIGN, "step 1");
        WorkflowResult result = new WorkflowResult();
        Map map = new Gson().fromJson("{\"response\": {\"body\": {\"msg\":\"this is result\"} }}", Map.class);
        result.setBody(map);
        process.setResult(result);
        instanceInfo.getStepProcessMap().put(process.getSign(), process);

        handler = new SchemaParamHandler(instanceInfo);
    }

    @Test
    public void parameters() throws Exception {
        Transfer transfer = schema.getSteps().get(0).getTransfer();

        List<Map<String, Object>> params = handler.parameters(transfer);
        System.out.println(params);
        assertEquals(INSTANCE_ID, params.get(0).get("instance-id"));

        int i = 0;
        for (Map<String, Object> p: params) {
            for (Map.Entry entry: p.entrySet()) {
                String name = entry.getValue().getClass().getSimpleName().toLowerCase();
                String actual = String.valueOf(transfer.getInput().get(i).getType()).toLowerCase();
                assertEquals(true, name.contains(actual));
                i++;
                System.out.println(String.format("Class name: %s, param type: %s", name, actual));
            }
        }
    }

    @Test
    public void convertStrParam() throws Exception {
        String param = String.format("{output}.%s.response.body", STEP_SIGN);
        String type = "map";

        Object result = handler.convertStrParam(param, type);
        System.out.println(result);
        assertEquals(true, Map.class.isAssignableFrom(result.getClass()));
    }

}