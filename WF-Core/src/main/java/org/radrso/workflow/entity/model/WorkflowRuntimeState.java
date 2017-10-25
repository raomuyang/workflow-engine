package org.radrso.workflow.entity.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.radrso.workflow.entity.StatusEnum;
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

    @Id
    private String workflowId;
    private String application;
    private StatusEnum status;
    private String msg;
}
