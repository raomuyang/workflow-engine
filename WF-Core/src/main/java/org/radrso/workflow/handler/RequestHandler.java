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
import org.radrso.plugins.requests.entity.ResponseCode;
import org.radrso.plugins.requests.entity.exceptions.impl.RequestException;
import org.radrso.workflow.constant.EngineConstant;
import org.radrso.workflow.constant.ExceptionCode;
import org.radrso.workflow.constant.RequestMethodMapping;
import org.radrso.workflow.entities.model.WorkflowResult;
import org.radrso.workflow.entities.schema.items.Step;
import org.radrso.workflow.resolvers.RequestResolver;

import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by raomengnan on 17-1-15.
 * 解析步骤的执行，请求URL或者调用指定方法
 */
@Log4j
public class RequestHandler {
    private Step step;
    private Object[] params;
    private String[] paramNames;

    public RequestHandler(Step step, List<Map<String, Object>> paramList){
        this.step = step;
        params = new Object[paramList.size()];
        paramNames = new String[paramList.size()];

        for (int i = 0; i < paramList.size(); ++i) {
            Map<String, Object> map = paramList.get(i);
            Object key = (map.keySet().toArray())[0];
            Object value = map.get(key);
            params[i] = value;
            paramNames[i] = (String) key;
        }
    }

    /**
     * 调用配置中预设的类方法，调用的方法入参如果带有可变长参数或基本参数的数组，则可能会失败，
     * 反射调用会对基本数据类型和包装数据类型自动装箱和拆箱，但不会对基本数据类型
     * 数组和包装数据类型的数组进行装箱和拆箱
     * 1. 工作流引擎在读取配置文件时，会将配置文件中的基础数据类型进行包装，这意味着如果指定调用的方法的参数中有
     *      数据类型数组与参数不匹配，可能不能自动装箱拆箱，会返回NoSuchMethodException错误
     * 2. 同理，如果指定的方法中带有基础类型的可变长参数，可变长参数相当于一个数组，同样
            无法自动拆箱
     *  3. 建议指定调用的方法入参类型为包装类型，会提高效率，避免异常
     *  4. 可以在配置文件中指定数组类型

     * @return  WFResponse中的Response是执行结果的消息实体
     */
    public WorkflowResult classRequest(){

        String className = null;
        String[] classStr  = step.getCall().split(":");
        if(classStr.length > 1)
            className = classStr[1];

        if(className == null){
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
            response.setCode(ResponseCode.HTTP_OK.code());
            response.setBody(ret);
            return response;

        } catch (ClassNotFoundException e) {
            log.warn(e);
            return new WorkflowResult(ExceptionCode.CLASS_NOT_FOUND.code(),
                    e.getMessage(), e.getMessage());

        } catch (NoSuchMethodException e) {
            log.error(e);
            return new WorkflowResult(ExceptionCode.METHOD_NOT_FOUND.code(),
                    e.getMessage(), e.getMessage());
        } catch (IllegalAccessException e) {
            log.error(e);
            return new WorkflowResult(ExceptionCode.METHOD_ACCESS_ERROR.code(),
                    e.getMessage(), e.getMessage());
        } catch (InvocationTargetException e) {
            log.error(e);
            return new WorkflowResult(ExceptionCode.METHOD_INVOCATION_ERROR.code(),
                    e.getMessage(), e.getMessage());
        } catch (InstantiationException e) {
            log.error(e);
            return new WorkflowResult(ExceptionCode.CLASS_INSTANCE_EXCEPTION.code(),
                    e.getMessage(), e.getMessage());
        } catch (IllegalArgumentException e){
            log.error(e);
            return new WorkflowResult(ExceptionCode.ILLEGAL_ARGMENT_EXCEPTION.code(),
                    e.getMessage(), e.getMessage());
        }


    }

    /**
     * 调用网络请求
     * @return  WFResponse中的Response是执行结果的消息实体
     */
    public WorkflowResult netRequest() {

        ContentType contentType = ContentType.APPLICATION_JSON;
        //获取请求方法 GET/PUT/POST/DELETE
        MethodEnum method;
        try {
            method = RequestMethodMapping.getMethod(step.getMethod());
        } catch (RequestException e) {
            log.error(e);
            return new WorkflowResult(ExceptionCode.UNSUPPORTED_REQUEST_METHOD.code(),
                    e.getMessage(), null);
        }

        //转换参数，配置文件中以$转义的，添加到header中
        Map<String, Object> paramMap = new HashMap<>();
        Map<String, Object> headers = new HashMap<>();
        Map<String, Object> urlParams = new HashMap<>();
        if(params == null)
            params = new Object[]{};
        for (int i = 0; i < params.length; i++){

            if(paramNames[i] != null && paramNames[i].startsWith("$")){
                if(paramNames[i].toLowerCase().equals("$content-type")){
                    String type = String.valueOf(params[i]);
                    String encoding = "utf-8";
                    if(type.contains(";") && !type.startsWith(";")) {
                        type = type.split(";")[0];
                        encoding = type.split(";")[1];
                    }
                    Charset charset;
                    try {
                        charset = Charset.forName(encoding);
                    }catch (UnsupportedCharsetException e){
                        charset = Charset.forName("utf-8");
                    }
                    contentType = ContentType.create(type, charset);
                    headers.put("Content-Type", contentType.toString());
                    continue;
                }
                headers.put(paramNames[i].substring(1), params[i]);
            }

            else if(paramNames[i].matches(EngineConstant.VALUES_ESCAPE)){
                String[] matchers = EngineConstant.matcherValuesEscape(paramNames[i]);
                if(matchers.length > 1)
                    paramMap.put(paramNames[i], params[i]);
                else {
                    String key = matchers[0];
                    urlParams.put(key, params[i]);
                }
            }
            else
                paramMap.put(paramNames[i], params[i]);
        }

        String url = step.getCall();
        String[] replaces = EngineConstant.matcherValuesEscape(url);
        if (replaces.length > 0)
            for(String key: replaces)
                url = url.replace(key, String.valueOf(urlParams.get(key)));
        step.setCall(url);

        return sendNetRequest(method, headers, paramMap, contentType);
    }

    private WorkflowResult sendNetRequest(MethodEnum method, Map<String, Object> headers, Map paramMap, ContentType contentType){
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
        if(!response.isSuccess())
            wfResponse.setMsg(response.getErrorMsg());

        wfResponse.setBody(response.getContent());
        return wfResponse;
    }


}
