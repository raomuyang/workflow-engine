package org.radrso.workflow;

import org.radrso.plugins.FileUtils;

import java.io.File;

/**
 * Created by raomengnan on 17-1-20.
 */
public interface StandardString {
     String PROVIDER_JAR_HOME = FileUtils.getProjectHome() + File.separator + "provider-jars" + File.separator;
     String SERVICE_JAR_HOME = FileUtils.getProjectHome() + File.separator + "service-jars" + File.separator;

     String CONF_START_SIGN = "&START";
     String CONF_FINISH_SIGN = "&FINISH";
     String CONF_INSTANCE_ID_VALUE = "&instanceid";

     String OUTPUT_VALUE = "{output}";
}
