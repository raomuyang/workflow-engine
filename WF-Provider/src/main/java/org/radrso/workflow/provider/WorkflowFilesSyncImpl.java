package org.radrso.workflow.provider;

import lombok.extern.log4j.Log4j;
import org.radrso.workflow.ConfigConstant;
import org.radrso.workflow.entities.config.JarFile;
import org.radrso.workflow.entities.response.WFResponse;
import org.radrso.workflow.exec.ActionCommand;
import org.radrso.workflow.rmi.WorkflowFilesSync;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    private JarFileRepository jarFileRepository;

    /**
     *
     * @param application
     * @param jarName
     * @return sunccess Code=200 | unsuccess Code=4046, 5003
     */
    @Override
    public WFResponse checkAndImportJar(String application, String jarName) {
        String fp = ROOT + application + File.separator;
        File file = new File(fp + jarName);
        if (!file.exists()) {
            JarFile jarFile = jarFileRepository.findByApplicationAndName(application, jarName);
            return ActionCommand.importJar(fp, jarName, jarFile.getFile());
        }
        return ActionCommand.checkAndImportJar(fp, jarName);
    }
}
