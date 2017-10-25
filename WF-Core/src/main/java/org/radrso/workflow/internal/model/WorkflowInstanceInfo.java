package org.radrso.workflow.internal.model;

import lombok.ToString;
import org.radrso.workflow.entities.StatusEnum;
import org.radrso.workflow.entities.model.Instance;
import org.radrso.workflow.entities.model.StepProgress;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * Created by raomengnan on 17-1-13.
 * 描述workflow实例的状态信息
 * branches         分支数（不包括当前分支）
 * branchStepMap    第n个分支与之对应的Step  sign(步骤标志)
 * stepStatusMap    tep的sign 与 StepStatus（完整的执行信息）的映射
 * stepProcess      Step的sign 与该 Step的执行进度(字符串)映射关系
 * finishedSequence 按各个Step完成顺序记录的step sign序列
 */
@ToString
public class WorkflowInstanceInfo {

    private Instance instance;

    private int branches;
    private List<String> cursor;
    private Map<String, StepProgress> stepProgressMap;
    private Queue<String> finishedSequence;

    public WorkflowInstanceInfo() {
        Instance i  = new Instance();
        i.setStatus(StatusEnum.CREATED);
        i.setCreateTime(new Date());
        init(i);
    }

    public WorkflowInstanceInfo(Instance instance) {
        init(instance);
    }

    private void init(Instance instance) {
        this.instance = instance;
        cursor = new ArrayList<>();
        stepProgressMap = new ConcurrentHashMap<>();
        finishedSequence = new ConcurrentLinkedDeque<>();
    }

    public String getInstanceId() {
        return instance.getInstanceId();
    }

    public void setInstanceId(String instanceId) {
        this.instance.setInstanceId(instanceId);
    }

    public String getWorkflowId() {
        return this.instance.getWorkflowId();
    }

    public void setWorkflowId(String workflowId) {
        this.instance.setWorkflowId(workflowId);
    }

    public Date getCreateTime() {
        return this.instance.getCreateTime();
    }

    public void setCreateTime(Date createTime) {
        this.instance.setCreateTime(createTime);
    }

    public Date getSubmitTime() {
        return this.instance.getSubmitTime();
    }

    public void setSubmitTime(Date submitTime) {
        this.instance.setSubmitTime(submitTime);
    }

    public StatusEnum getStatus() {
        return this.instance.getStatus();
    }

    public void setStatus(StatusEnum status) {
        this.instance.setStatus(status);
    }

    public int getBranches() {
        return branches;
    }

    public void setBranches(int branches) {
        this.branches = branches;
    }

    public List<String> getCursor() {
        return cursor;
    }

    public void setCursor(List<String> cursor) {
        this.cursor = cursor;
    }

    public Map<String, StepProgress> getStepProgressMap() {
        return stepProgressMap;
    }

    public void setStepProgressMap(Map<String, StepProgress> stepProgressMap) {
        this.stepProgressMap = stepProgressMap;
    }

    public Queue<String> getFinishedSequence() {
        return finishedSequence;
    }

    public void setFinishedSequence(Queue<String> finishedSequence) {
        this.finishedSequence = finishedSequence;
    }
}
