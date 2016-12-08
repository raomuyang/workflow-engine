package org.radrso.utils;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by raomengnan on 16-12-2.
 */
public class BeanFactoryUtils {
    private volatile static ClassPathXmlApplicationContext ctx_provider;

    public final static String APPLICATION_CONTEXT_ROOT = "";
    public final static String APPLICATION_CONTEXT_PATH = APPLICATION_CONTEXT_ROOT + "applicationContext.xml";

    public static void init(){
        if(ctx_provider == null){
            String[] configLocations = new String[]{APPLICATION_CONTEXT_PATH};
            ctx_provider = new ClassPathXmlApplicationContext(configLocations);
        }
    }

    public static ClassPathXmlApplicationContext getContext(){
        init();
        return ctx_provider;
    }
}
