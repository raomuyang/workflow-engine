package org.radrso.workflow.entity.model;

import lombok.Data;
import lombok.ToString;
import org.radrso.workflow.entity.StatusEnum;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.*;

/**
 * Created by raomengnan on 17-1-13.
 * 描述workflow实例的状态信息
 * branches          分支数（不包括当前分支）
 * branchStepMap    第n个分支与之对应的Step  sign(步骤标志)
 * stepStatusMap    tep的sign 与 StepStatus（完整的执行信息）的映射
 * stepProcess      Step的sign 与该 Step的执行进度(字符串)映射关系
 * finishedSequence 按各个Step完成顺序记录的step sign序列
 */
@Data
@ToString
@Document(collection = "workflowInstance")
public class WorkflowInstance implements Serializable{
    private String workflowId;
    @Id
    private String instanceId;
    private Date createTime = new Date();
    private Date submitTime;
    private StatusEnum status = StatusEnum.CREATED;

    private int branches = 0;
    private Map<Integer, String> branchStepMap;

    private Map<String, StepProgress> stepStatusesMap;
    private Map<String, String> stepProcess;
    private List<String> finishedSequence;

    public WorkflowInstance(String workflowId, String instanceId){
        this();
        this.workflowId = workflowId;
        this.instanceId = instanceId;
    }
    private WorkflowInstance(){
        this.stepStatusesMap = new HashMap<>();
        this.stepProcess = new HashMap<>();
        this.branchStepMap = new HashMap<>();
        this.finishedSequence = new ArrayList<>();
    }
}
