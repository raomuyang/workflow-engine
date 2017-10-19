package org.radrso.workflow.base;

import org.radrso.workflow.entities.schema.items.Step;
import org.radrso.workflow.entities.response.WFResponse;

import java.io.Serializable;

/**
 * Created by rao-mengnan on 2017/5/18.
 */
public interface Operations extends Serializable {

    WFResponse executeStepAction(Step step, Object[] params, String[] paramNames);

    WFResponse checkAndImportJar(String application, String jarName);

}
