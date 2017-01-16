package org.radrso.workflow.entities.wf;

import lombok.Data;
import org.radrso.workflow.entities.response.WFResponse;

import java.io.Serializable;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by raomengnan on 17-1-13.
 */
@Data
public class WorkflowInstance implements Serializable{
    public static final String EXPIRED = "expired";
    public static final String RUNNING = "running";
    public static final String COMPLETED = "completed";
    public static final String EXCEPTION = "exception";

    private String applicationId;
    private String instanceId;
    private Date createTime = new Date();
    private Date submitTime;
    private String status = RUNNING;

    private ConcurrentHashMap<String, String> stepProcess;
    private ConcurrentHashMap<String, Object[]> stepParams;
    private ConcurrentHashMap<String, String[]> stepParamNames;
    private ConcurrentHashMap<String, WFResponse> stepResponses;

    public WorkflowInstance(String applicationId, String instanceId){
        this();
        this.applicationId = applicationId;
        this.instanceId = instanceId;
    }
    public WorkflowInstance(){
        this.stepProcess = new ConcurrentHashMap<>();
        this.stepParams = new ConcurrentHashMap<>();
        this.stepResponses = new ConcurrentHashMap<>();
        this.stepParamNames = new ConcurrentHashMap<>();
    }
}
