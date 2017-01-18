package org.radrso.workflow.entities.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.radrso.workflow.entities.config.items.Step;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by raomengnan on 16-12-8.
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Document(collection = "workflow")
public class WorkflowConfig implements Serializable{
    private String application;
    @Id
    private String id;
    private String owner;
    private String header;
    private Date startTime;
    private Date stopTime;
    private List<Step> steps;
}
