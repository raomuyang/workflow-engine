package org.radrso.plugins;

import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandlerFactory;

/**
 * Created by raomengnan on 17-1-2.
 */
public class CustomClassLoader extends URLClassLoader {
    public CustomClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    public CustomClassLoader(URL[] urls, ClassLoader parent, URLStreamHandlerFactory factory) {
        super(urls, parent, factory);
    }

    public CustomClassLoader(URL[] urls) {
        super(urls);
    }
}
