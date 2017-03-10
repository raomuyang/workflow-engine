import com.google.gson.Gson;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.radrso.workflow.entities.config.WorkflowConfig;
import org.radrso.workflow.entities.config.items.Transfer;
import org.radrso.workflow.entities.exceptions.ConfigReadException;
import org.radrso.workflow.entities.exceptions.UnknowExceptionInRunning;
import org.radrso.workflow.entities.response.WFResponse;
import org.radrso.workflow.entities.wf.StepStatus;
import org.radrso.workflow.entities.wf.WorkflowInstance;
import org.radrso.workflow.resolvers.WorkflowResolver;

import java.util.HashMap;

/**
 * Created by rao-mengnan on 2017/3/10.
 */

public class TestWorkflowResolver {
    WorkflowResolver workflowResolver;
    WorkflowInstance workflowInstance;
    WorkflowConfig workflowConfig;

    @Before
    public void before(){
        workflowConfig = new Gson().fromJson(wf, WorkflowConfig.class);
        workflowInstance = new WorkflowInstance("workflow-test", "instance-test");
        workflowResolver = new WorkflowResolver(workflowConfig, workflowInstance);

    }

    @Test
    public void testGetParam() throws UnknowExceptionInRunning, ConfigReadException {

        Transfer transfer = workflowConfig.getSteps().get(0).getTransfer();
        workflowResolver.getParams(transfer);

        StepStatus stepStatus_1 = workflowResolver.getStepStatusMap().get("sign-1");
        Assert.assertEquals(stepStatus_1.getParams()[0].getClass(), String.class);
        Assert.assertEquals(stepStatus_1.getParams()[1].getClass(), Double.class);
        Assert.assertEquals(stepStatus_1.getParams()[2].getClass(), Integer.class);

        WFResponse response_1 = new WFResponse();
        response_1.setCode(200);
        response_1.setMsg("OK");
        HashMap<String, String> body = new HashMap<>();
        body.put("test1", "test");
        body.put("test2", "1.23");
        response_1.setResponse(body);
        stepStatus_1.setWfResponse(response_1);

        // step2的参数根据step1的输出结果确定
        transfer = workflowConfig.getSteps().get(1).getTransfer();
        Object[] params_2 = workflowResolver.getParams(transfer);
        Assert.assertEquals(params_2[1].getClass(), Double.class);

    }

    static String wf = "{\n" +
            "  \"application\": \"application-test\",\n" +
            "  \"id\": \"wf-test\",\n" +
            "  \"startTime\":\"2016-01-01\",\n" +
            "  \"stopTime\":\"2017-12-01\",\n" +
            "\n" +

            "  \"steps\": [\n" +

            "    {\n" +
            "      \"sign\": \"$START\",\n" +
            "      \"name\": \"Start\",\n" +
            "      \"transfer\":{\n" +
            "        \"input\": [\n" +
            "          {\"name\": \"test1\",  \"type\": \"String\", \"value\": \"test\"},\n" +
            "          {\"name\": \"test2\",  \"type\": \"double\", \"value\": 11},\n" +
            "          {\"name\": \"test3\",  \"type\": \"int\", \"value\": 2}\n" +
            "        ],\n" +
            "\n" +
            "        \"to\": \"sign-1\"\n" +
            "      }\n" +
            "    },\n" +


            "    {\n" +
            "      \"sign\": \"sign-1\",\n" +
            "      \"name\": \"step-test-1\",\n" +
            "      \"call\": \"class:org.radrso.test.TestWorkflow\",\n" +
            "      \"method\": \"testStep1\",\n" +
            "      \"loop\": 1,\n" +
            "\n" +
            "      \"transfer\": {\n" +

            "        \"input\": [\n" +
            "          {\"name\": \"test1\",  \"type\": \"String\", \"value\": \"{output}[sign-1][test1]\"},\n" +
            "          {\"name\": \"test2\",  \"type\": \"double\", \"value\": \"{output}[sign-1][test2]\"}\n"+
            "        ],\n" +

            "        \"to\": \"sign-3\"  \n" +

            "      }\n" +
            "    },\n" +


            "    {\n" +
            "      \"sign\": \"sign-2\",\n" +
            "      \"name\": \"step-test-2\",\n" +
            "      \"call\": \"class:org.radrso.test.TestWorkflow\",\n" +
            "      \"method\": \"testStep2\",\n" +
            "      \"loop\": 1,\n" +
            "\n" +
            "      \"transfer\": {\n" +
            "\n" +
            "        \"judge\":\n" +
            "        {\n" +
            "          \"compute\": \"{output}[sign-2][test2]\",\n" +
            "          \"computeWith\": 1000,\n" +
            "          \"type\": \"double\",\n" +
            "          \"expression\": \"<\",\n" +
            "          \"passTransfer\":{\n" +
            "            \"input\":[],\n" +
            "            \"to\": \"sign-3\",\n" +
            "            \"scatters\":[\"$FINISH\"]\n" +
            "          },\n" +
            "          \"nopassTransfer\":{\n" +
            "            \"input\":[],\n" +
            "            \"to\": \"start\"\n" +
            "          }\n" +
            "        }\n" +
            "      }\n" +
            "    },\n" +


            "    {\n" +
            "      \"sign\": \"sign-3\",\n" +
            "      \"name\": \"step-test-5\",\n" +
            "      \"call\": \"class:org.radrso.test.TestWorkflow\",\n" +
            "      \"method\": \"testStep5\",\n" +
            "      \"loop\": 1,\n" +
            "\n" +
            "      \"transfer\": {\n" +
            "\n" +
            "        \"diedline\":\"2017-11-12\",\n" +
            "        \"to\": \"$FINISH\"\n" +
            "      }\n" +
            "    },\n" +



            "    {\n" +
            "      \"sign\": \"$FINISH\",\n" +
            "      \"name\": \"Finish the workflow\",\n" +
            "      \"call\": \"class:org.radrso.test.TestWorkflow\",\n" +
            "      \"method\": \"finish\"\n" +
            "    }\n" +
            "  ]\n" +

            "}";
}
