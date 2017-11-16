package org.radrso.workflow.entity.schema.items;

import com.google.gson.annotations.SerializedName;
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
 * thanTransfer     表达式为true时的下个转移transfer
 * elseTransfer   表达式为false时的下个转移transfer
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Switch implements Serializable{
    private Object variable;
    @SerializedName("compare_to")
    private Object compareTo;
    private String type;
    @SerializedName("if")
    private String expression;
    @SerializedName("than")
    private Transfer thanTransfer;
    @SerializedName("else")
    private Transfer elseTransfer;
}
