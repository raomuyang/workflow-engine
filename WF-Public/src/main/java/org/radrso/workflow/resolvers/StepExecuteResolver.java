package org.radrso.workflow.resolvers;

import com.google.gson.JsonObject;
import lombok.extern.log4j.Log4j;
import org.apache.http.entity.ContentType;
import org.radrso.workflow.RequestMethodMapping;
import org.radrso.workflow.entities.config.items.Step;
import org.radrso.workflow.entities.response.WFResponse;
import org.radrso.plugins.CustomClassLoader;
import org.radrso.plugins.JsonUtils;
import org.radrso.plugins.ReflectInvokeMethod;
import org.radrso.plugins.requests.Request;
import org.radrso.plugins.requests.RequestFactory;
import org.radrso.plugins.requests.entity.Method;
import org.radrso.plugins.requests.entity.Response;
import org.radrso.plugins.requests.entity.exceptions.ResponseCode;
import org.radrso.plugins.requests.entity.exceptions.impl.RequestException;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by raomengnan on 17-1-15.
 */
@Log4j
public class StepExecuteResolver {
    private Step step;
    private Object[] params;
    private String[] paramNames;

    public StepExecuteResolver(Step step, Object[] params, String[] paramNames){
        this.step = step;
        this.params = params;
        this.paramNames = paramNames;
    }

    public WFResponse classRequest(){

        String className = null;
        String[] classStr  = step.getCall().split(":");
        if(classStr.length > 1)
            className = classStr[1];

        if(className == null){
            String errorMsg = ResponseCode.UNKNOW.info() + String.format("[%s, %s]", "ClassName split error", step.getCall());
            log.error(errorMsg);
            return new WFResponse(ResponseCode.UNKNOW.code(), errorMsg, errorMsg);
        }

        String mehtodName = step.getMethod();
        log.info("Invoke: " + className + "-" + mehtodName);
        try {
            WFResponse response = new WFResponse();
            Class clazz = CustomClassLoader.getClassLoader().loadClass(className);
            Object ret = ReflectInvokeMethod.invoke(clazz, clazz.newInstance(), mehtodName, new Object[]{params});
            response.setCode(ResponseCode.OK.code());
            response.setResponse(ret);
            return response;

        } catch (ClassNotFoundException e) {
            log.error(e);
            return new WFResponse(ResponseCode.CLASS_NOT_FOUND.code(),
                    e.getMessage(), e.getMessage());

        } catch (NoSuchMethodException e) {
            log.error(e);
            return new WFResponse(ResponseCode.METHOD_NOT_FOUND.code(),
                    e.getMessage(), e.getMessage());
        } catch (IllegalAccessException e) {
            log.error(e);
            return new WFResponse(ResponseCode.METHOD_ACCESS_ERROR.code(),
                    e.getMessage(), e.getMessage());
        } catch (InvocationTargetException e) {
            log.error(e);
            return new WFResponse(ResponseCode.METHOD_INVOCATION_ERROR.code(),
                    e.getMessage(), e.getMessage());
        } catch (InstantiationException e) {
            log.error(e);
            return new WFResponse(ResponseCode.CLASS_INSTANCE_EXCEPTION.code(),
                    e.getMessage(), e.getMessage());
        }


    }

    public WFResponse netReuqest() {

        //获取请求方法 GET/PUT/POST/DELETE
        Method method = null;
        try {
            method = RequestMethodMapping.getMethod(step.getMethod());
        } catch (RequestException e) {
            log.error(e);
            return new WFResponse(ResponseCode.UNSUPPORTED_REQUEST_METHOD.code(),
                    e.getMessage(), null);
        }

        //转换参数
        Map<String, Object> paramMap = new HashMap<>();
        if(params == null)
            params = new Object[]{};
        for (int i = 0; i < params.length; i++)
            paramMap.put(paramNames[i], params[i]);

        // 通过请求工厂创建请求，发送请求
        RequestFactory requestFactory = new RequestFactory(
                step.getCall(),
                method,
                JsonUtils.getJsonObject(paramMap),
                ContentType.APPLICATION_JSON,
                true
        );
        Request request = null;
        Response response = null;
        try {
            request = requestFactory.createRequest();
            response = request.sendRequest();
        } catch (RequestException e) {
            log.error(e);
            return new WFResponse(e.getCode().code(), e.getMessage(), null);
        }


        // 解析Http/Https请求返回的数据
        WFResponse wfResponse = new WFResponse();
        wfResponse.setCode(response.getStatusCode());
        if(!response.isSuccess())
            wfResponse.setMsg(response.getErrorMsg());
        try {
            if(response.getContentType().equals(ContentType.APPLICATION_JSON.toString())){
                JsonObject object = JsonUtils.getJsonObject(response.getContent());
                wfResponse.setResponse(object);
            }
            else
                wfResponse.setResponse(response.getContent());
        }catch (Throwable e){
            log.error(String.format("[%s]", response.getContent()) + e.getMessage());
            wfResponse.setResponse(response.getContent());
        }

        return wfResponse;
    }

}
