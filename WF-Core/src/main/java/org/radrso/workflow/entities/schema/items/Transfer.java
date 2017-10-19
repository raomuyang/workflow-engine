package org.radrso.workflow.entities.schema.items;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by raomengnan on 16-12-8.
 * 状态转移函数
 * judge    内部的判断函数
 * deadline 截止时间
 * to       下一个Step的sign（id）
 * scatters 从此处产生新的分支
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Transfer implements Serializable{
    private List<InputItem> input;
    private Switch aSwitch;
    private Date deadline;
    private String to;
    private List<Transfer> scatters;
}
