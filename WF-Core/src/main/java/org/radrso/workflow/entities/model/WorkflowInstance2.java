package org.radrso.workflow.entities.model;

import lombok.Data;
import lombok.ToString;
import org.radrso.workflow.entities.StatusEnum;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

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
public class WorkflowInstance2 {
    @Id
    private String instanceId;
    private String workflowId;
    private Date createTime;
    private Date submitTime;
    private StatusEnum status = StatusEnum.CREATED;

    private int branches;
    private List<String> cursor;
    private Map<String, StepProcess> stepProcessMap;
    private Queue<String> finishedSequence;

    public WorkflowInstance2() {
        createTime = new Date();
        cursor = new ArrayList<>();
        stepProcessMap = new ConcurrentHashMap<>();
        finishedSequence = new ConcurrentLinkedDeque<>();
    }
}
