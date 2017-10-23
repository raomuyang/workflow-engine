package org.radrso.workflow.base;

import org.radrso.workflow.entities.schema.items.Step;
import org.radrso.workflow.entities.model.WorkflowResult;

import java.io.Serializable;

/**
 * Created by rao-mengnan on 2017/5/18.
 */
public interface Operations extends Serializable {

    WorkflowResult executeStepAction(Step step, Object[] params, String[] paramNames);

    WorkflowResult checkAndImportJar(String application, String jarName);

}
