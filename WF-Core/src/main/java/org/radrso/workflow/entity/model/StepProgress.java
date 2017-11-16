package org.radrso.workflow.entity.model;

import lombok.Data;
import lombok.ToString;
import org.radrso.workflow.entity.StatusEnum;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by raomengnan on 17-2-9.
 * sign         Step标志（ID）
 * status       Step执行状态
 * params       Step参数列表
 * result       Step执行后的返回结果
 */
@Data
@ToString
@Document(collection = "progress")
public class StepProgress implements Serializable{
    @Id
    private String id;
    private String sign;
    private String instanceId;
    private String name;
    private StatusEnum status;
    private Date begin;
    private Date end;
    private List<Map<String, Object>> params;
    private WorkflowResult result;
    private String precursor;

    public StepProgress(String instanceId, String sign, String name) {
        this.instanceId = instanceId;
        this.sign = sign;
        this.name = name;
        this.status = StatusEnum.WAIT;
    }
}
