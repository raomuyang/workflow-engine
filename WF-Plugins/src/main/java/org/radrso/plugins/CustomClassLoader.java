package org.radrso.plugins;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandlerFactory;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by raomengnan on 17-1-2.
 */
public class CustomClassLoader extends URLClassLoader {

    private static volatile CustomClassLoader customClassLoader;

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println("[SHUTDOWNHOOK] CLOSE CLASSLOADER");
                    getClassLoader().close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }));
    }

    public static CustomClassLoader getClassLoader() {

        if (customClassLoader == null) {
            URL[] urls = new URL[]{};
            customClassLoader = new CustomClassLoader(urls);
        }
        return customClassLoader;
    }

    private CustomClassLoader(URL[] urls) {
        super(urls);
    }

    private CustomClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    private CustomClassLoader(URL[] urls, ClassLoader parent, URLStreamHandlerFactory factory) {
        super(urls, parent, factory);
    }


    public void addJar(URL url) {
        this.addURL(url);
    }

    public void addJar(File file) throws MalformedURLException {
        URL url = file.toURL();
        this.addURL(url);
    }


}
