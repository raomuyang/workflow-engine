package org.radrso.workflow.provider;

import org.radrso.workflow.base.Operations;
import org.radrso.workflow.constant.EngineConstant;
import org.radrso.workflow.entity.model.JarFile;
import org.radrso.workflow.entity.schema.items.Step;
import org.radrso.workflow.entity.model.WorkflowResult;
import org.radrso.workflow.handler.PackageImportHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;

/**
 * Created by rao-mengnan on 2017/5/18.
 */
@Service
public class OperationsImpl implements Operations {
    public static final String ROOT = EngineConstant.PROVIDER_JAR_HOME;
    @Autowired
    private JarFileRepository jarFileRepository;

    @Override
    public WorkflowResult executeStepAction(Step step, Object[] params, String[] paramNames) {
        return PackageImportHandler.execute(step, params, paramNames);
    }

    @Override
    public WorkflowResult checkAndImportJar(String application, String jarName) {
        String fp = ROOT + application + File.separator;
        File file = new File(fp + jarName);
        if (!file.exists()) {
            JarFile jarFile = jarFileRepository.findByApplicationAndName(application, jarName);
            return PackageImportHandler.importJar(fp, jarName, jarFile.getFile());
        }
        return PackageImportHandler.importLocalJar(fp, jarName);
    }
}
