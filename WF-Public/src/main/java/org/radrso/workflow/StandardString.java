package org.radrso.workflow;

import org.radrso.plugins.FileUtils;

import java.io.File;

/**
 * Created by raomengnan on 17-1-20.
 */
public interface StandardString {
     String PROVIDER_JAR_HOME = FileUtils.getProjectHome() + File.separator + "provider-jars" + File.separator;
     String SERVICE_JAR_HOME = FileUtils.getProjectHome() + File.separator + "service-jars" + File.separator;
}
