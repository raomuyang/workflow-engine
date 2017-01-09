package org.radrso.core;

import org.radrso.entities.response.WFResponse;

/**
 * Created by raomengnan on 17-1-4.
 * 向provider传输文件，返回文件id
 */
public interface File2Remote {

    WFResponse importJar(String application, String jarName, byte[] stream);

    WFResponse importConfigFile(byte[] stream);

    WFResponse importFileArg(byte[] stream);
}
