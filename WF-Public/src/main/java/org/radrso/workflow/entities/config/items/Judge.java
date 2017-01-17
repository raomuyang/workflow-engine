package org.radrso.workflow.entities.config.items;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 * Created by raomengnan on 16-12-8.
 * compare  要比较的对象
 * judge    判断条件  >  =  <  >=  <=
 * next     if true, next
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Judge implements Serializable{
    private Object compute;
    private Object computeWith;
    private String type;
    private String expression;
    private String next;
    private Transfer passTransfer;
    private Transfer nopassTransfer;
}
