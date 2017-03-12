package org.radrso.workflow.resolvers;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import lombok.extern.log4j.Log4j;
import org.apache.http.entity.ContentType;
import org.radrso.workflow.RequestMethodMapping;
import org.radrso.workflow.StandardString;
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
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
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

    /**
     * 调用配置中预设的类方法
     * @return  WFResponse中的Response是执行结果的消息实体
     */
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

        String methodName = step.getMethod();
        log.info("Invoke: " + className + "-" + methodName);
        try {
            WFResponse response = new WFResponse();
            Class clazz = CustomClassLoader.getClassLoader().loadClass(className);
            Object ret = ReflectInvokeMethod.invoke(clazz, clazz.newInstance(), methodName, params);
            response.setCode(ResponseCode.HTTP_OK.code());
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


    public WFResponse netRequest() {

        ContentType contentType = ContentType.APPLICATION_JSON;
        //获取请求方法 GET/PUT/POST/DELETE
        Method method = null;
        try {
            method = RequestMethodMapping.getMethod(step.getMethod());
        } catch (RequestException e) {
            log.error(e);
            return new WFResponse(ResponseCode.UNSUPPORTED_REQUEST_METHOD.code(),
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
                    Charset charset = Charset.forName("utf-8");
                    try {
                        charset = Charset.forName(encoding);
                    }catch (UnsupportedCharsetException e){}
                    contentType = ContentType.create(type, charset);
                    headers.put("Content-Type", contentType.toString());
                    continue;
                }
                headers.put(paramNames[i].substring(1), params[i]);
            }

            else if(paramNames[i].matches(StandardString.VALUES_ESCAPE)){
                String[] matchers = StandardString.matcherValuesEscape(paramNames[i]);
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
        String[] replaces = StandardString.matcherValuesEscape(url);
        if (replaces.length > 0)
            for(String key: replaces)
                url = url.replace(key, String.valueOf(urlParams.get(key)));
        step.setCall(url);

        return sendNetRequest(method, headers, paramMap, contentType);
    }

    private WFResponse sendNetRequest(Method method, Map headers, Map paramMap, ContentType contentType){
        // 通过请求工厂创建请求，发送请求
        Request request = null;
        Response response = null;
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
            return new WFResponse(e.getCode().code(), e.getMessage(), null);
        }


        // 解析Http/Https请求返回的数据
        WFResponse wfResponse = new WFResponse();
        wfResponse.setCode(response.getStatusCode());
        if(!response.isSuccess())
            wfResponse.setMsg(response.getErrorMsg());
        try {
            try {
                JsonObject object = JsonUtils.getJsonElement(response.getContent()).getAsJsonObject();
                wfResponse.setResponse(object);
            }catch (JsonParseException e){
                log.debug(e);
                wfResponse.setResponse(response.getContent());
            }

        }catch (Throwable e){
            log.error(String.format("[%s]", response.getContent()) + e.getMessage());
            wfResponse.setResponse(response.getContent());
        }

        return wfResponse;
    }


}
