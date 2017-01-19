package org.radrso.workflow.entities.wf;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by raomengnan on 17-1-19.
 */
@Document(collection = "WorkflowErrorLog")
@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
public class WorkflowErrorLog implements Serializable{
    private String _id;
    private String workflowId;
    private String instanceId;
    private String stepSign;
    private String msg;
    private Date date;
    private Throwable exception;
}
