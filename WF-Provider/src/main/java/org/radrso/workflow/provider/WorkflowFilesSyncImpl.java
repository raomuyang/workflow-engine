package org.radrso.workflow.provider;

import lombok.extern.log4j.Log4j;
import org.radrso.workflow.ConfigConstant;
import org.radrso.workflow.entities.response.WFResponse;
import org.radrso.workflow.exec.ActionCommand;
import org.radrso.workflow.rmi.WorkflowFilesSync;
import org.springframework.stereotype.Service;

import java.io.File;

/**
 * Created by raomengnan on 17-1-17.
 * 同步Jar包等文件
 */
@Log4j
@Service
public class WorkflowFilesSyncImpl implements WorkflowFilesSync {
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
        return ActionCommand.importJar(path, jarName, stream);
    }

    @Override
    public WFResponse checkAndImportJar(String application, String jarName) {
        String fp = ROOT + application + File.separator + jarName;
        return ActionCommand.checkAndImportJar(fp, jarName);
    }
}
