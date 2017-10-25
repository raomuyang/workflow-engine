package org.radrso.workflow.entities.schema;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.radrso.workflow.entities.schema.items.Step;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.ArrayList;
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
public class WorkflowSchema implements Serializable{
    private String application;
    @Id
    private String id;
    private String owner;
    @SerializedName("start_time")
    private Date startTime;
    @SerializedName("stop_time")
    private Date stopTime;
    private List<Step> steps;
    private List<String> jars = new ArrayList<>();
}
