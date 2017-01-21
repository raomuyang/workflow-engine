### Simple workflow config
```
{
  "application": "application-test",
  "id": "wf-test",
  "startTime":"2016-01-01",
  "stopTime":"2017-12-01",

  "steps": [
    {
      "sign": "{START}",
      "name": "Start",
      "transfer":{
        "input": [
          {"name": "test1",  "type": "String", "value": "test"},
          {"name": "test2",  "type": "double", "value": 11},
          {"name": "test3",  "type": "int", "value": 2}
        ],

        "to": "sign-1"
      }
    },

    {
      "sign": "sign-1",
      "name": "step-test-1",
      "call": "class:org.radrso.test.TestWorkflow",
      "method": "testStep1",
      "loop": 1,

      "transfer": {

        "judge":
        {
          "compute": "{output}[sign-1][test2]",
          "computeWith": 1234,
          "type": "double",
          "expression": "<",
          "passTransfer":{
            "input":[],
            "to": "sign-2"
          },
          "nopassTransfer":{
            "input":[],
            "to": "sign-3"
          }
        }

      }
    },

    {
      "sign": "sign-2",
      "name": "step-test-2",
      "call": "class:org.radrso.test.TestWorkflow",
      "method": "testStep2",
      "loop": 1,

      "transfer": {

        "judge":
        {
          "compute": "{output}[sign-2][test2]",
          "computeWith": 1000,
          "type": "double",
          "expression": "<",
          "passTransfer":{
            "input":[],
            "to": "sign-3",
            "scatters":["{FINISH}"]
          },
          "nopassTransfer":{
            "input":[],
            "to": "start"
          }
        }
      }
    },

    {
      "sign": "sign-3",
      "name": "step-test-3",
      "call": "class:org.radrso.test.TestWorkflow",
      "method": "testStep3",
      "loop": 1,

      "transfer": {

        "judge":
        {
          "compute": "{output}[sign-3][test1]",
          "computeWith": "a",
          "type": "String",
          "expression": "=",

          "passTransfer":{
            "input": [
              {"name": "test1",  "type": "boolean", "value": true},
              {"name": "test2",  "type": "int", "value": 1},
              {"name": "test3",  "type": "string", "value": "test"}
            ],

            "to": "sign-4"
          },

          "nopassTransfer":{
            "input":[],
            "to": "sign-3"
          }
        }

      }
    },


    {
      "sign": "sign-4",
      "name": "step-test-4",
      "call": "class:org.radrso.test.TestWorkflow",
      "method": "testStep4",
      "loop": 1,

      "transfer": {
        "input": [
          {"name": "test1",  "type": "int", "value": 1},
          {"name": "test2",  "type": "string", "value": "test"},
          {"name": "test3",  "type": "boolean", "value": true}
        ],

        "diedline":"2017-11-12",
        "to": "sign-5"
      }
    },


    {
      "sign": "sign-5",
      "name": "step-test-5",
      "call": "class:org.radrso.test.TestWorkflow",
      "method": "testStep5",
      "loop": 1,

      "transfer": {

        "diedline":"2017-11-12",
        "to": "{FINISH}"
      }
    },

    {
      "sign": "{FINISH}",
      "name": "Finish the workflow",
      "call": "class:org.radrso.test.TestWorkflow",
      "method": "finish"
    }
  ],

  "header": "request header,在一些http请求的任务中可能会附带的",
  "jars": ["Test-1.0.jar"]
}
```

> 当一个新的分支建立时，除了建立的瞬间和当前分支的转移函数有联系，在之后的运行都是相对独立的