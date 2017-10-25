package org.radrso.workflow.handler;

import lombok.extern.log4j.Log4j;
import org.radrso.plugins.CustomClassLoader;
import org.radrso.plugins.FileUtils;
import org.radrso.plugins.requests.entity.ResponseCode;
import org.radrso.workflow.constant.ExceptionCode;
import org.radrso.workflow.entities.model.WorkflowResult;

import java.io.File;
import java.io.IOException;


/**
 * Created by rao-mengnan
 * on 2017/3/14.
 */

@Log4j
public final class PackageImportHandler {
    public static final String RESPONSE = "success";

    /**
     * 导入jar文件
     *
     * @param dir     jar文件的路径，不包含jar文件本身
     * @param jarName jar文件文件名
     * @param stream  jar文件的字节数组
     * @return WFResponse中包含操作的结果及信息
     */
    public static WorkflowResult importJar(String dir, String jarName, byte[] stream) {
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
            return new WorkflowResult(ExceptionCode.UNKNOW.code(), e.getMessage(), e);
        }

        return new WorkflowResult(ResponseCode.HTTP_OK.code(), null, RESPONSE);
    }

    public static WorkflowResult importLocalJar(String dir, String jarName) {
        String path = dir + File.separator + jarName;
        File file = new File(path);
        if (file.exists()) {
            log.info(String.format("[Import] add local existed jar_file[%s]", path));
            try {
                return importJar(dir, jarName, FileUtils.getByte(file));
            } catch (IOException e) {
                log.error(e);
                return new WorkflowResult(ExceptionCode.UNKNOW.code(), ExceptionCode.UNKNOW.info(), null);
            }
        } else {
            return new WorkflowResult(ExceptionCode.JAR_FILE_NOT_FOUND.code(), ExceptionCode.JAR_FILE_NOT_FOUND.info(), null);
        }
    }
}
