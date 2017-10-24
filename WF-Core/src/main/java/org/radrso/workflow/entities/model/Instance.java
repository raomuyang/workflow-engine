package org.radrso.workflow.entities.model;

import lombok.Data;
import lombok.ToString;
import org.radrso.workflow.entities.StatusEnum;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * Created by Rao-Mengnan
 * on 2017/10/24.
 */
@Data
@ToString
@Document(collection = "instances")
public class Instance {
    @Id
    private String instanceId;
    private String workflowId;
    private Date createTime;
    private Date submitTime;
    private StatusEnum status = StatusEnum.CREATED;
}
