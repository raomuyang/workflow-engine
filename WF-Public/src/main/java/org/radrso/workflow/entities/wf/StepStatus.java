package org.radrso.workflow.entities.wf;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.radrso.workflow.entities.response.WFResponse;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by raomengnan on 17-2-9.
 */
@Getter
@Setter
public class StepStatus implements Serializable{
    private String sign;
    private String status;
    private Date begin;
    private Date end;
    private Object[] params;
    private String[] paramNames;
    private WFResponse wfResponse;

    public StepStatus(String sign) {
        this.sign = sign;
    }
}
