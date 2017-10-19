package org.radrso.workflow.exec;

import lombok.extern.log4j.Log4j;
import org.radrso.plugins.CustomClassLoader;
import org.radrso.plugins.FileUtils;
import org.radrso.plugins.requests.entity.ResponseCode;
import org.radrso.workflow.constant.ExceptionCode;
import org.radrso.workflow.entities.schema.items.Step;
import org.radrso.workflow.entities.response.WFResponse;
import org.radrso.workflow.resolvers.StepActionResolver;
import org.radrso.workflow.resolvers.Resolvers;

import java.io.File;
import java.io.IOException;


/**
 * Created by rao-mengnan on 2017/3/14.
 */
@Log4j
public final class BaseOperations {
    public static final String RESPONSE = "success";

    /**
     * 导入jar文件
     *
     * @param dir     jar文件的路径，不包含jar文件本身
     * @param jarName jar文件文件名
     * @param stream  jar文件的字节数组
     * @return WFResponse中包含操作的结果及信息
     */
    public static WFResponse importJar(String dir, String jarName, byte[] stream) {
        String path = dir + File.separator + jarName;
        boolean added;
        try {
            log.info(String.format("[Import] Write and import jar_file[%s]", path));
            added = FileUtils.writeFile(dir, jarName, stream);
            if (added)
                CustomClassLoader.getClassLoader().addJar(new File(path));
            log.info(String.format("[Import] Success:Write and import jar_file[%s]", path));
        } catch (IOException e) {
            log.error("[Import] Failed:" + e);
            return new WFResponse(ExceptionCode.UNKNOW.code(), e.getMessage(), e);
        }

        return new WFResponse(ResponseCode.HTTP_OK.code(), null, RESPONSE);
    }

    public static WFResponse checkAndImportJar(String dir, String jarName) {
        String path = dir + File.separator + jarName;
        File file = new File(path);
        if (file.exists()) {
            log.info(String.format("[Import] add local existed jar_file[%s]", path));
            try {
                return importJar(dir, jarName, FileUtils.getByte(file));
            } catch (IOException e) {
                log.error(e);
                return new WFResponse(ExceptionCode.UNKNOW.code(), ExceptionCode.UNKNOW.info(), null);
            }
        } else {
            return new WFResponse(ExceptionCode.JAR_FILE_NOT_FOUND.code(), ExceptionCode.JAR_FILE_NOT_FOUND.info(), null);
        }
    }

    /**
     * 执行工作流的一个步骤
     *
     * @param step       当前执行的Step对象
     * @param params     启动这个步骤的参数
     * @param paramNames 启动这个步骤的参数名，与参数顺序相同
     * @return 返回WFResponse，封装对象的response属性才是执行结果
     */
    public static WFResponse execute(Step step, Object[] params, String[] paramNames) {
        StepActionResolver resolver = Resolvers.getStepActionResolver(step, params, paramNames);
        if (step.getCall() == null || step.getCall().indexOf(":") < 0) {
            return new WFResponse(ResponseCode.HTTP_BAD_REQUEST.code(), "Error Protocol:" + step.getCall(), null);
        }
        String protocol = step.getCall().substring(0, step.getCall().indexOf(":"));

        WFResponse response = null;
        if (protocol.toLowerCase().indexOf("http") >= 0)
            response = resolver.netRequest();
        else
            response = resolver.classRequest();
        return response;
    }
}
