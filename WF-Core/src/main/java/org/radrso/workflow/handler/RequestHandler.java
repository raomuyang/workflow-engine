package org.radrso.workflow.handler;

import lombok.extern.log4j.Log4j;
import org.apache.http.entity.ContentType;
import org.radrso.plugins.CustomClassLoader;
import org.radrso.plugins.JsonUtils;
import org.radrso.plugins.ReflectInvokeMethod;
import org.radrso.plugins.requests.BaseRequest;
import org.radrso.plugins.requests.RequestFactory;
import org.radrso.plugins.requests.entity.MethodEnum;
import org.radrso.plugins.requests.entity.Response;
import org.radrso.plugins.requests.entity.exceptions.impl.RequestException;
import org.radrso.workflow.constant.EngineConstant;
import org.radrso.workflow.constant.ExceptionCode;
import org.radrso.workflow.constant.RequestMethodMapping;
import org.radrso.workflow.constant.WFStatusCode;
import org.radrso.workflow.entity.model.WorkflowResult;
import org.radrso.workflow.entity.schema.items.Step;

import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by raomengnan on 17-1-15.
 * FSM中的输出函数，执行当前的步骤:http请求或class方法执行
 */
@Log4j
public class RequestHandler {
    private Step step;

    private Object[] params;
    private String[] paramNames;

    Map<String, Object> httpParamMap = new HashMap<>();
    Map<String, Object> httpHeaders = new HashMap<>();
    ContentType contentType = ContentType.APPLICATION_JSON;

    public RequestHandler(Step step, List<Map<String, Object>> paramList) {
        this.step = step;

        if (paramList.size() > 0) {
            params = new Object[paramList.size()];
            paramNames = new String[paramList.size()];
        } else {
            params = new Object[0];
            paramNames = new String[0];

        }
        for (int i = 0; i < paramList.size(); ++i) {
            Map<String, Object> map = paramList.get(i);
            Object key = (map.keySet().toArray())[0];
            Object value = map.get(key);
            params[i] = value;
            paramNames[i] = (String) key;
        }
    }

    public WorkflowResult handle() {
        WorkflowResult result;
        if (step.getCall() == null || !step.getCall().contains(":")) {
            result = new WorkflowResult(WFStatusCode.HTTP_BAD_REQUEST.code(), "Error Protocol:" + step.getCall(), null);
        } else {
            String protocol = step.getCall().substring(0, step.getCall().indexOf(":"));

            if (protocol.toLowerCase().contains("http"))
                result = netRequest();
            else
                result = classRequest();

        }
        return result;
    }

    /**
     * 调用配置中预设的类方法，调用的方法入参如果带有可变长参数或基本参数的数组，则可能会失败，
     * 反射调用会对基本数据类型和包装数据类型自动装箱和拆箱，但不会对基本数据类型
     * 数组和包装数据类型的数组进行装箱和拆箱
     * 1. 工作流引擎在读取配置文件时，会将配置文件中的基础数据类型进行包装，这意味着如果指定调用的方法的参数中有
     * 数据类型数组与参数不匹配，可能不能自动装箱拆箱，会返回NoSuchMethodException错误
     * 2. 同理，如果指定的方法中带有基础类型的可变长参数，可变长参数相当于一个数组，同样
     * 无法自动拆箱
     * 3. 建议指定调用的方法入参类型为包装类型，会提高效率，避免异常
     * 4. 可以在配置文件中指定数组类型
     *
     * @return WFResponse中的Response是执行结果的消息实体
     */
    public WorkflowResult classRequest() {

        String className = null;
        String[] classStr = step.getCall().split(":");
        if (classStr.length > 1)
            className = classStr[1];

        if (className == null) {
            String errorMsg = ExceptionCode.UNKNOW.info() + String.format("[%s, %s]", "ClassName split error", step.getCall());
            log.error(errorMsg);
            return new WorkflowResult(ExceptionCode.UNKNOW.code(), errorMsg, errorMsg);
        }

        String methodName = step.getMethod();
        log.info("[Invoke] " + className + "-" + methodName);
        try {
            WorkflowResult response = new WorkflowResult();
            Class clazz = CustomClassLoader.getClassLoader().loadClass(className);
            Object ret = ReflectInvokeMethod.invoke(clazz, clazz.newInstance(), methodName, params);
            response.setCode(WFStatusCode.OK.code());
            response.setBody(ret);
            return response;

        } catch (ClassNotFoundException e) {
            log.warn(e);
            return new WorkflowResult(WFStatusCode.CLASS_NOT_FOUND.code(),
                    e.getMessage(), null);
        } catch (NoSuchMethodException e) {
            log.error(e);
            return new WorkflowResult(WFStatusCode.METHOD_NOT_FOUND.code(),
                    e.getMessage(), null);
        } catch (IllegalAccessException e) {
            log.error(e);
            return new WorkflowResult(WFStatusCode.METHOD_ACCESS_ERROR.code(),
                    e.getMessage(), null);
        } catch (InvocationTargetException e) {
            log.error(e);
            return new WorkflowResult(WFStatusCode.METHOD_INVOCATION_ERROR.code(),
                    e.getMessage(), null);
        } catch (InstantiationException e) {
            log.error(e);
            return new WorkflowResult(WFStatusCode.CLASS_INSTANCE_EXCEPTION.code(),
                    e.getMessage(), null);
        } catch (IllegalArgumentException e) {
            log.error(e);
            return new WorkflowResult(WFStatusCode.ILLEGAL_ARGUMENT_EXCEPTION.code(),
                    e.getMessage(), null);
        }


    }

    /**
     * 调用网络请求
     *
     * @return WFResponse中的Response是执行结果的消息实体
     */
    public WorkflowResult netRequest() {
        //获取请求方法 GET/PUT/POST/DELETE
        MethodEnum method;
        try {
            method = RequestMethodMapping.getMethod(step.getMethod());
        } catch (RequestException e) {
            log.error(e);
            return new WorkflowResult(WFStatusCode.UNSUPPORTED_REQUEST_METHOD.code(),
                    e.getMessage(), null);
        }

        initRequestInfo();

        return sendNetRequest(method, httpHeaders, httpParamMap, contentType);
    }

    void initRequestInfo() {
        //转换参数，配置文件中以$转义的，添加到header中
        for (int i = 0; i < params.length; i++) {
            String name = paramNames[i];
            Object value = params[i];

            if (putHeader(name, value)) {
                continue;
            }

            // url placeholder
            if (setUrlPlaceholder(name, value)) {
                continue;
            }
            httpParamMap.put(paramNames[i], params[i]);
        }
    }

    private boolean putHeader(String name, Object value) {
        if (name != null && name.startsWith(EngineConstant.HEADER_PARAMS_ESCAPE)) {
            if (name.toLowerCase().equals(EngineConstant.CONTENT_TYPE_PARAM_NAME)) {
                String type = String.valueOf(value);
                String encoding = EngineConstant.DEFAULT_ENCODING;
                if (type.contains(";") && !type.startsWith(";")) {
                    type = String.valueOf(value).split(";")[0];
                    encoding = String.valueOf(value).split(";")[1];
                }
                Charset charset;
                try {
                    charset = Charset.forName(encoding);
                } catch (UnsupportedCharsetException e) {
                    charset = Charset.forName(EngineConstant.DEFAULT_ENCODING);
                }
                contentType = ContentType.create(type, charset);
                httpHeaders.put("Content-Type", contentType.toString());
                return true;
            }

            if (name.length() > 1) {
                httpHeaders.put(name.substring(1), value);
                return true;
            }
        }
        return false;
    }

    private boolean setUrlPlaceholder(String name, Object value) {
        if (name.matches(EngineConstant.VALUES_ESCAPE)) {
            String[] matches = EngineConstant.matcherValuesEscape(name);
            if (matches.length > 1) return false;
            else {
                String key = matches[0];
                String url = step.getCall();
                url = url.replace(key, String.valueOf(value));
                step.setCall(url);
                return true;
            }
        }
        return false;
    }

    private WorkflowResult sendNetRequest(MethodEnum method, Map<String, Object> headers, Map paramMap, ContentType contentType) {
        // 通过请求工厂创建请求，发送请求
        BaseRequest request;
        Response response;
        try {
            request = RequestFactory.createRequest(
                    step.getCall(),
                    method,
                    headers,
                    JsonUtils.getJsonElement(paramMap),
                    contentType,
                    true
            );
            response = request.sendRequest();
        } catch (RequestException e) {
            log.error(e);
            return new WorkflowResult(e.getCode().code(), e.getMessage(), null);
        }


        // 解析Http/Https请求返回的数据
        WorkflowResult wfResponse = new WorkflowResult();
        wfResponse.setCode(response.getStatusCode());
        if (!response.isSuccess())
            wfResponse.setMsg(response.getErrorMsg());

        wfResponse.setBody(response.getContent());
        return wfResponse;
    }


}
