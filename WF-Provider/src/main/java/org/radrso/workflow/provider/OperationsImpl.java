package org.radrso.workflow.provider;

import org.radrso.workflow.base.Operations;
import org.radrso.workflow.constant.ConfigConstant;
import org.radrso.workflow.entities.config.JarFile;
import org.radrso.workflow.entities.config.items.Step;
import org.radrso.workflow.entities.response.WFResponse;
import org.radrso.workflow.exec.BaseOperations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;

/**
 * Created by rao-mengnan on 2017/5/18.
 */
@Service
public class OperationsImpl implements Operations {
    public static final String ROOT = ConfigConstant.PROVIDER_JAR_HOME;
    @Autowired
    private JarFileRepository jarFileRepository;

    @Override
    public WFResponse executeStepAction(Step step, Object[] params, String[] paramNames) {
        return BaseOperations.execute(step, params, paramNames);
    }

    @Override
    public WFResponse checkAndImportJar(String application, String jarName) {
        String fp = ROOT + application + File.separator;
        File file = new File(fp + jarName);
        if (!file.exists()) {
            JarFile jarFile = jarFileRepository.findByApplicationAndName(application, jarName);
            return BaseOperations.importJar(fp, jarName, jarFile.getFile());
        }
        return BaseOperations.checkAndImportJar(fp, jarName);
    }
}
