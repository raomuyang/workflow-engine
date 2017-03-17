package org.radrso.workflow.resolvers;

import com.google.gson.Gson;
import org.junit.Before;
import org.junit.Test;
import org.radrso.workflow.entities.config.WorkflowConfig;
import org.radrso.workflow.entities.wf.WorkflowInstance;
import org.radrso.workflow.resolvers.impl.ParamsResolver;
import org.radrso.workflow.resolvers.impl.WorkflowConfigResolver;

/**
 * Created by rao-mengnan on 2017/3/17.
 */
public class TestDemo {
    WorkflowConfigResolver workflowResolver;
    WorkflowInstance workflowInstance;
    WorkflowConfig workflowConfig;
    ParamsResolver paramsResolver;

    @Before
    public void before(){
        workflowConfig = new Gson().fromJson(demo, WorkflowConfig.class);
        workflowInstance = new WorkflowInstance("workflow-test", "instance-test");
        workflowResolver = new WorkflowConfigResolver(workflowConfig, workflowInstance);
        paramsResolver = new ParamsResolver(workflowInstance);
    }

    @Test
    public void testConfigResolver(){
        System.out.println(workflowConfig);
    }

    private static final String demo =
            "{\n" +
                    "  \"application\": \"Daisy-oneday\",\n" +
                    "  \"id\": \"daisy\",\n" +
                    "  \"startTime\": \"2016-01-01\",\n" +
                    "  \"stopTime\": \"2999-01-01\",\n" +
                    "\n" +
                    "  \"steps\": [\n" +
                    "    {\n" +
                    "      \"sign\": \"&START\",\n" +
                    "      \"name\": \"One day start\",\n" +
                    "      \"transfer\":{\n" +
                    "        \"input\": [\n" +
                    "          {\"type\": \"String\", \"value\": \"Daisy\"}\n" +
                    "        ],\n" +
                    "        \"to\": \"id-hello-daisy\"\n" +
                    "      }\n" +
                    "    },\n" +
                    "\n" +
                    "    {\n" +
                    "      \"sign\": \"id-hello-daisy\",\n" +
                    "      \"name\": \"Robot say hello to Daisy\",\n" +
                    "      \"call\": \"class:org.radrso.workflow.jobdemo.Hello\",\n" +
                    "      \"method\": \"hello\",\n" +
                    "\n" +
                    "      \"transfer\": {\n" +
                    "        \"input\": [\n" +
                    "          {\"name\": \"{key}\", \"value\": \"99ezvsj9m86eozbt\"},\n" +
                    "          {\"name\": \"{city}\", \"value\": \"北京\"}\n" +
                    "        ],\n" +
                    "        \"to\": \"id-search-weather\",\n" +
                    "\n" +
                    "        \"scatters\": [\n" +
                    "          {\n" +
                    "            \"input\": [{\"type\": \"String\", \"value\": \"Mike\"}],\n" +
                    "            \"to\": \"id-wake-up-mike\"\n" +
                    "          }\n" +
                    "        ]\n" +
                    "      }\n" +
                    "    },\n" +
                    "\n" +
                    "    {\n" +
                    "      \"sign\": \"id-wake-up-mike\",\n" +
                    "      \"name\": \"Wake Mike up\",\n" +
                    "      \"call\": \"class:org.radrso.workflow.jobdemo.Hello\",\n" +
                    "      \"method\": \"wakeup\",\n" +
                    "      \"transfer\": {\n" +
                    "        \"input\": [{\"name\": \"list\", \"type\": \"Integer[]\", \"value\": \"[3, 2, 1, 4, 5, 7, 6]\"}],\n" +
                    "        \"to\": \"id-mike-home-work\"\n" +
                    "      }\n" +
                    "    },\n" +
                    "\n" +
                    "    {\n" +
                    "      \"sign\": \"id-search-weather\",\n" +
                    "      \"name\": \"Search city weather\",\n" +
                    "      \"call\": \"https://api.thinkpage.cn/v3/weather/now.json?key={key}&location={city}&language=zh-Hans&unit=c\",\n" +
                    "      \"method\": \"Get\",\n" +
                    "      \"transfer\": {\n" +
                    "        \"judge\": {\n" +
                    "          \"compute\": \"{output}[id-search-weather]\",\n" +
                    "          \"computeWith\": \"晴天\",\n" +
                    "          \"type\": \"String\",\n" +
                    "          \"expression\": \"=\",\n" +
                    "\n" +
                    "          \"passTransfer\":{\n" +
                    "            \"input\": [\n" +
                    "              {\"name\": \"info\", \"value\": \"北京周边哪里好玩\"},\n" +
                    "              {\"name\": \"key\", \"value\": \"\"}\n" +
                    "            ],\n" +
                    "            \"to\": \"id-outing\"\n" +
                    "          },\n" +
                    "          \"nopassTransfer\":{\n" +
                    "            \"input\": [\n" +
                    "              {\"name\": \"info\", \"value\": \"红烧肉怎么做\"},\n" +
                    "              {\"name\": \"key\", \"value\": \"\"}\n" +
                    "            ],\n" +
                    "            \"to\": \"id-cooking\"\n" +
                    "          }\n" +
                    "        }\n" +
                    "      }\n" +
                    "    },\n" +
                    "\n" +
                    "    {\n" +
                    "      \"sign\": \"id-cooking\",\n" +
                    "      \"name\": \"Cooking in the house\",\n" +
                    "      \"call\": \"http://www.tuling123.com/openapi/api\",\n" +
                    "      \"method\": \"Post\",\n" +
                    "\n" +
                    "      \"transfer\":{\n" +
                    "        \"to\": \"&FINISH\"\n" +
                    "      }\n" +
                    "    },\n" +
                    "\n" +
                    "    {\n" +
                    "      \"sign\": \"id-outing\",\n" +
                    "      \"name\": \"Outing\",\n" +
                    "      \"call\": \"http://www.tuling123.com/openapi/api\",\n" +
                    "      \"method\": \"Post\",\n" +
                    "\n" +
                    "      \"transfer\":{\n" +
                    "        \"to\": \"&FINISH\"\n" +
                    "      }\n" +
                    "    },\n" +
                    "\n" +
                    "    {\n" +
                    "      \"sign\": \"id-mike-home-work\",\n" +
                    "      \"name\": \"Mike do homework\",\n" +
                    "      \"call\": \"class:org.radrso.workflow.jobdemo.SimpleSort\",\n" +
                    "      \"method\": \"quickSort\",\n" +
                    "      \"transfer\": {\n" +
                    "        \"to\": \"&FINISH\"\n" +
                    "      }\n" +
                    "    },\n" +
                    "\n" +
                    "    {\n" +
                    "      \"sign\": \"&FINISH\",\n" +
                    "      \"name\": \"Finish the day\"\n" +
                    "    }\n" +
                    "  ],\n" +
                    "\n" +
                    "  \"jars\": [\"org.radrso.workflow.jobdemo-1.0.jar\"]\n" +
                    "}";
}
