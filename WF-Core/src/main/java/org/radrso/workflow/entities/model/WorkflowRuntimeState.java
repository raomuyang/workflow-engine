package org.radrso.workflow.entities.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

/**
 * Created by raomengnan on 17-1-16.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Document(collection = "runtime")
public class WorkflowRuntimeState implements Serializable{
    public static final String CREATED = "created";
    public static final String START = "started";
    public static final String STOP = "stopped";
    public static final String EXCEPTION = "exception";

    @Id
    private String workflowId;
    private String application;
    private String status;
    private String msg;
}
