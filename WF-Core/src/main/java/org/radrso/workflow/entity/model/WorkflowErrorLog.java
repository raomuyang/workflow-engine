package org.radrso.workflow.entity.model;

import lombok.Data;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by raomengnan on 17-1-19.
 */
@Document(collection = "WorkflowErrorLog")
@Data
@ToString
public class WorkflowErrorLog implements Serializable{
    private String _id;
    private int code;
    private String workflowId;
    private String instanceId;
    private String stepSign;
    private String msg;
    private Date date;
    private String detailMsg;
    public WorkflowErrorLog(){
        this.date = new Date();
    }

    public WorkflowErrorLog(String _id, int code, String workflowId, String instanceId, String stepSign, String msg, Date date, Throwable exception) {
        this._id = _id;
        this.code = code;
        this.workflowId = workflowId;
        this.instanceId = instanceId;
        this.stepSign = stepSign;
        this.msg = msg;
        this.date = date;
        this.detailMsg = getDetail(exception);
    }

    private String getDetail(Throwable e){
        Throwable cause = e.getCause();
        String msg = e.getMessage();
        if (msg == null || msg.equals("")){
            msg = e.toString();
        }
        if (cause != null) {
            return msg + "\n" + getDetail(cause);
        }
        return msg;
    }
}
