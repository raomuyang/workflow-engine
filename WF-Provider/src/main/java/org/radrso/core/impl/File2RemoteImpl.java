package org.radrso.core.impl;

import org.radrso.core.File2Remote;
import org.radrso.entities.response.WFResponse;
import org.radrso.plugins.CustomClassLoader;
import org.radrso.plugins.FileUtils;
import org.radrso.plugins.requests.entity.exceptions.ResponseCode;

import java.io.File;
import java.io.IOException;

/**
 * Created by raomengnan on 17-1-5.
 */
public class File2RemoteImpl implements File2Remote {

    @Override
    public WFResponse importJar(String application, String jarName, byte[] stream) {
        WFResponse response = new WFResponse();
        String root = FileUtils.getProjectHome();

        if(application == null || application.equals("")){
            response.setCode(ResponseCode.BAD_REQUEST.code());
            response.setMsg("Nullpoint");
            return response;
        }else {
            try {
                String path = root + application + File.separator;
                boolean succ = FileUtils.writeFile(path, jarName, stream);

                if (succ){
                    File jar = new File(path + jarName);
                    CustomClassLoader classLoader = CustomClassLoader.getClassLoader();
                    classLoader.addJar(jar);
                }


            } catch (IOException e) {
                response.setCode(ResponseCode.UNKNOW.code());
                response.setMsg(e.getMessage());
                return response;
            }

            response.setCode(ResponseCode.OK.code());
            response.setMsg("Success to save jar package");
            return response;
        }

    }

    @Override
    public WFResponse importConfigFile(byte[] stream) {
        return null;
    }

    @Override
    public WFResponse importFileArg(byte[] stream) {
        return null;
    }

}
