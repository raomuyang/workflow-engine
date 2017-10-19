package org.radrso.workflow.entities.info;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by raomengnan on 17-2-9.
 * sign         Step标志（ID）
 * status       Step执行状态
 * params       当前Step参数列表
 * paramsNames  当前Step参数的参数名，顺序与params list一直
 * wfResponse   当前Step执行后的返回结果
 */
@Getter
@Setter
@ToString
public class StepStatus implements Serializable{
    private String sign;
    private String name;
    private String status;
    private Date begin;
    private Date end;
    private Object[] params;
    private String[] paramNames;
    private WorkflowResult wfResponse;

    public StepStatus(String sign, String name) {
        this.sign = sign;
        this.name = name;
    }
}
