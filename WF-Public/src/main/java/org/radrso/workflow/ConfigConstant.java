package org.radrso.workflow;

import org.radrso.plugins.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by raomengnan on 17-1-20.
 */
public interface ConfigConstant {
     String PROVIDER_JAR_HOME = FileUtils.getProjectHome() + File.separator + "provider-jars" + File.separator;
     String SERVICE_JAR_HOME = FileUtils.getProjectHome() + File.separator + "service-jars" + File.separator;

     String CONF_START_SIGN = "&START";
     String CONF_FINISH_SIGN = "&FINISH";
     String CONF_INSTANCE_ID_VALUE = "{instanceid}";

     String OUTPUT_VALUE = "{output}";

     String HEADER_PARAMS_ESCAPE = "$";
     String VALUES_ESCAPE = "\\{(.*?)\\}";

     static String[] matcherValuesEscape(String str){
          return matcherValuesEscape(str, 0);
     }
     static String[] matcherValuesEscape(String str, int group){
          Pattern pattern = Pattern.compile(VALUES_ESCAPE);
          Matcher matcher = pattern.matcher(str);
          List<String> list = new ArrayList<>();
          while (matcher.find())
               list.add(matcher.group(group));
          if(list.size() == 0)
               return new String[]{};
          String[]  ret = new String[list.size()];
          return list.toArray(ret);
     }
}
