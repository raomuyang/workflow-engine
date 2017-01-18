package org.radrso.workflow.provider;

import org.radrso.plugins.CustomClassLoader;
import org.radrso.plugins.FileUtils;
import org.radrso.plugins.requests.entity.exceptions.ResponseCode;
import org.radrso.workflow.entities.response.WFResponse;
import org.radrso.workflow.rmi.WorkflowCommander;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

/**
 * Created by raomengnan on 17-1-17.
 */
@Service
public class WorkflowCommanderImpl implements WorkflowCommander{
    @Override
    public WFResponse importJar(String workflowId, String jarName, byte[] stream) {
        String path = FileUtils.getProjectHome() + File.separator + workflowId + File.separator;
        boolean add = false;
        try {
            add = FileUtils.writeFile(path, jarName, stream);
            if(add)
                CustomClassLoader.getClassLoader().addJar(new File(path + File.separator + jarName));
        } catch (IOException e) {
            return new WFResponse(ResponseCode.UNKNOW_HOST_EXCEPTION.code(), e.getMessage(), e);
        }

        return new WFResponse(ResponseCode.HTTP_OK.code(), null, "success");
    }
}
