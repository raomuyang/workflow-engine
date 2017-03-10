package org.radrso.workflow.entities.config.items;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 * Created by raomengnan on 16-12-8.
 * compute          计算对象，可以是指定值或者某个Step的输出
 * computeWith      另一个计算对象，同上
 * type             计算对象的类型
 * expression       表达式  >  =  <  >=  <=
 * passTransfer     表达式为true时的下个转移transfer
 * nopassTransfer   表达式为false时的下个转移transfer
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
