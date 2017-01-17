package org.radrso.workflow.entities.config.items;

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
 * wait 表示等待的时间
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Transfer implements Serializable{
    private List<InputItem> input;
    private Judge judge;
    private Date diedline;
    private String to;
    private List<String> scatters;
}
