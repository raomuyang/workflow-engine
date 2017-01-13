package org.radrso.entities.wf;

import com.google.gson.JsonObject;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * Created by raomengnan on 17-1-13.
 */
@Data
public class WorkflowInstance implements Serializable{
    private String applicationId;
    private String instanceId;
    private Date createTime;
    private Date submitTime;
    private Map<String, String> stepProcess;
    private Map<String, JsonObject> stepResponses;
}
