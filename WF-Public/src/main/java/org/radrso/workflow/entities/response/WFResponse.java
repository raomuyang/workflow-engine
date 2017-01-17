package org.radrso.workflow.entities.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 * Created by raomengnan on 17-1-4.
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
public class WFResponse implements Serializable{
    private int    code;
    private String msg;
    private Object response;
}
