package org.radrso.workflow.provider;

import org.radrso.plugins.CustomClassLoader;
import org.radrso.plugins.FileUtils;
import org.radrso.plugins.requests.entity.exceptions.ResponseCode;
import org.radrso.workflow.entities.response.WFResponse;
import org.radrso.workflow.rmi.WorkflowCommander;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

/**
 * Created by raomengnan on 17-1-17.
 */
@Service
public class WorkflowCommanderImpl implements WorkflowCommander{
    public static final String root = FileUtils.getProjectHome() + File.separator + "provide-jars" + File.separator;

    /**
     *
     * @param application
     * @param jarName
     * @param stream
     * @return sunccess Code=200 | unsuccess Code=5003
     */
    @Override
    public WFResponse importJar(String application, String jarName, byte[] stream) {
        String path = root + application + File.separator;
        boolean add = false;
        try {
            add = FileUtils.writeFile(path, jarName, stream);
            if(add)
                CustomClassLoader.getClassLoader().addJar(new File(path + jarName));
        } catch (IOException e) {
            return new WFResponse(ResponseCode.UNKNOW_HOST_EXCEPTION.code(), e.toString(), e);
        }

        return new WFResponse(ResponseCode.HTTP_OK.code(), null, "success");
    }

    @Override
    public WFResponse checkAndImportJar(String application, String jarName) {
        String fp = root + application + File.separator + jarName;
        File file = new File(fp);
        if (file.exists())
            return importJar(application, jarName, FileUtils.getByte(file));
        else
            return new WFResponse(ResponseCode.JAR_FILE_NOT_FOUND.code(), ResponseCode.JAR_FILE_NOT_FOUND.info(), null);
    }
}
