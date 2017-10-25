package org.radrso.workflow.constant;

import org.radrso.plugins.FileUtils;
import org.radrso.plugins.JsonUtils;
import org.radrso.workflow.entities.Properties;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by raomengnan on 17-1-20.
 * 工作流引擎中用到的常量和静态方法
 */
public class EngineConstant {
     private static final Properties properties;

     static {
          String path = EngineConstant.class.getResource("/engine-properties.json").getFile();
          properties = JsonUtils.loadJsonFile(path, Properties.class);
     }

     public static final String DEFAULT_ENCODING = "utf-8";
     public static final String CONTENT_TYPE_PARAM_NAME = "$content-type";

     public static final String PROVIDER_JAR_HOME = FileUtils.getProjectHome() + File.separator + "provider-jars" + File.separator;
     public static final String SERVICE_JAR_HOME = FileUtils.getProjectHome() + File.separator + "service-jars" + File.separator;

     public static final String SCHEMA_START_SIGN = properties.getSchemaStartSign();
     public static final String SCHEMA_FINISH_SIGN = properties.getSchemaFinishSign();
     public static final String SCHEMA_INSTANCE_ID_VALUE = properties.getSchemaInstanceIdValue();

     public static final String OUTPUT_VALUE = properties.getOutputValue();

     public static final String HEADER_PARAMS_ESCAPE = properties.getHeaderParamsEscape();
     public static final String VALUES_ESCAPE = properties.getValuesEscape();

     private static final Pattern VALUES_ESCAPE_PATTERN = Pattern.compile(VALUES_ESCAPE);

     public static String[] matcherValuesEscape(String str){
          return matcherValuesEscape(str, 0);
     }
     public static String[] matcherValuesEscape(String str, int group){
          Matcher matcher = VALUES_ESCAPE_PATTERN.matcher(str);
          List<String> list = new ArrayList<>();
          while (matcher.find())
               list.add(matcher.group(group));
          if(list.size() == 0)
               return new String[]{};
          String[]  ret = new String[list.size()];
          return list.toArray(ret);
     }
}
