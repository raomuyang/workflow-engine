package org.radrso.workflow.provider;

import lombok.extern.log4j.Log4j;
import org.radrso.plugins.CustomClassLoader;
import org.radrso.plugins.FileUtils;
import org.radrso.plugins.requests.entity.exceptions.ResponseCode;
import org.radrso.workflow.ConfigConstant;
import org.radrso.workflow.entities.response.WFResponse;
import org.radrso.workflow.rmi.WorkflowCommander;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

/**
 * Created by raomengnan on 17-1-17.
 */
@Log4j
@Service
public class WorkflowCommanderImpl implements WorkflowCommander{
    public static final String ROOT = ConfigConstant.PROVIDER_JAR_HOME;

    /**
     *
     * @param application
     * @param jarName
     * @param stream
     * @return sunccess Code=200 | unsuccess Code=5003
     */
    @Override
    public WFResponse importJar(String application, String jarName, byte[] stream) {
        log.info(String.format("Import jar[%s]", application + "/" + jarName));
        String path = ROOT + application + File.separator;
        boolean add = false;
        try {
            add = FileUtils.writeFile(path, jarName, stream);
            if(add)
                CustomClassLoader.getClassLoader().addJar(new File(path + jarName));
        } catch (IOException e) {
            log.error("[Import] " + e);
            return new WFResponse(ResponseCode.UNKNOW_HOST_EXCEPTION.code(), e.toString(), e);
        }

        return new WFResponse(ResponseCode.HTTP_OK.code(), null, "success");
    }

    @Override
    public WFResponse checkAndImportJar(String application, String jarName) {
        String fp = ROOT + application + File.separator + jarName;
        File file = new File(fp);
        if (file.exists()){
            log.info(String.format("Import local jar[%s]", application + "/" + jarName));
            return importJar(application, jarName, FileUtils.getByte(file));
        }
        else
            return new WFResponse(ResponseCode.JAR_FILE_NOT_FOUND.code(), ResponseCode.JAR_FILE_NOT_FOUND.info(), null);
    }
}
