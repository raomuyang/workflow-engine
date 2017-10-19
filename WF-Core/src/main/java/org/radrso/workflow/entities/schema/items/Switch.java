package org.radrso.workflow.entities.schema.items;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 * Created by raomengnan on 16-12-8.
 * variable         对比对象，可以是指定值或者某个Step的输出
 * compareTo        另一个计算对象，同上
 * type             计算对象的类型
 * expression       表达式  >  =  <  >=  <=
 * ifTransfer     表达式为true时的下个转移transfer
 * elseTransfer   表达式为false时的下个转移transfer
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Switch implements Serializable{
    private Object variable;
    private Object compareTo;
    private String type;
    private String expression;
    private Transfer ifTransfer;
    private Transfer elseTransfer;
}
