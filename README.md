[![Build Status](https://travis-ci.org/raomuyang/workflow-engine.svg?branch=master)](https://travis-ci.org/raomuyang/workflow-engine)  

### 调用指定的类方法时须知
* 反射调用会对基本数据类型和包装数据类型自动装箱和拆箱，但不会对基本数据类型
数组和包装数据类型的数组进行装箱和拆箱  
> 1. 工作流引擎在读取配置文件时，若未在配置文件中指定参数数组类型，会将配置文件中的基础数据类型进行包装，这意味着如果指定调用的方法的参数中有基础
数据类型数组，如果发生混用，就不能自动装箱拆箱，会返回NoSuchMethodException错误
> 2. 同理，如果指定的方法中带有基础类型的可变长参数，可变长参数相当于一个数组，同样
无法自动拆箱
> 3. 建议指定调用的方法入参类型为包装类型，会提高效率，避免异常
> 4. 直接在参数中指定参数数组类型，同样可以避免

### Simple workflow config
```
{
  "application": "Daisy-oneday",
  "id": "daisy",
  "startTime": "2016-01-01",
  "stopTime": "2999-01-01",

  "steps": [
    {
      "sign": "&START",
      "name": "One day start",
      "transfer":{
        "input": [
          {"type": "String", "value": "Daisy"}
        ],
        "to": "id-hello-daisy"
      }
    },

    {
      "sign": "id-hello-daisy",
      "name": "Robot say hello to Daisy",
      "call": "class:org.radrso.workflow.jobdemo.Hello",
      "method": "hello",

      "transfer": {
        "input": [
          {"name": "{key}", "value": "99ezvsj9m86eozbt"},
          {"name": "{city}", "value": "北京"}
        ],
        "to": "id-search-weather",

        "scatters": [
          {
            "input": [{"type": "String", "value": "Mike"}],
            "to": "id-wake-up-mike"
          }
        ]
      }
    },

    {
      "sign": "id-wake-up-mike",
      "name": "Wake Mike up",
      "call": "class:org.radrso.workflow.jobdemo.Hello",
      "method": "wakeup",
      "transfer": {
        "input": [{"name": "list", "type": "Integer[]", "value": "[3, 2, 1, 4, 5, 7, 6]"}],
        "to": "id-mike-home-work"
      }
    },

    {
      "sign": "id-search-weather",
      "name": "Search city weather",
      "call": "https://api.thinkpage.cn/v3/weather/now.json?key={key}&location={city}&language=zh-Hans&unit=c",
      "method": "Get",
      "transfer": {
        "judge": {
          "variable": "{output}[id-search-weather][results][0][now][text]",
          "compareTo": "晴天",
          "type": "String",
          "expression": "=",

          "ifTransfer":{
            "input": [
              {"name": "info", "value": "北京周边哪里好玩"},
              {"name": "key", "value": ""}
            ],
            "to": "id-outing"
          },
          "elseTransfer":{
            "input": [
              {"name": "info", "value": "红烧肉怎么做"},
              {"name": "key", "value": ""}
            ],
            "to": "id-cooking"
          }
        }
      }
    },

    {
      "sign": "id-cooking",
      "name": "Cooking in the house",
      "call": "http://www.tuling123.com/openapi/api",
      "method": "Post",

      "transfer":{
        "input":[{"type": "String", "value": "Daisy"}],
        "to": "&FINISH"
      }
    },

    {
      "sign": "id-outing",
      "name": "Outing",
      "call": "http://www.tuling123.com/openapi/api",
      "method": "Post",

      "transfer":{
        "input":[{"type": "String", "value": "Daisy"}],
        "to": "&FINISH"
      }
    },

    {
      "sign": "id-mike-home-work",
      "name": "Mike do homework",
      "call": "class:org.radrso.workflow.jobdemo.SimpleSort",
      "method": "quickSort",
      "transfer": {
        "input":[{"type": "String", "value": "Mike"}],
        "to": "&FINISH"
      }
    },

    {
      "sign": "&FINISH",
      "name": "Finished the day",
      "call": "class:org.radrso.workflow.jobdemo.Hello",
      "method": "finish"
    }
  ],

  "jars": ["org.radrso.workflow.jobdemo-1.0.jar"]
}
```

> 当一个新的分支建立时，除了建立的瞬间和当前分支的转移函数有联系，在之后的运行都是相对独立的