package org.radrso.entities.config.items;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

/**
 * Created by raomengnan on 16-12-8.
 * 用于组成“下一步”和一些“判断条件”
 * conditionItems   判断条件的数组
 * nextStep         下一步，break到output，则nextStep=0
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Transfer {
    private List<InputItem> input;
    private List<Judge> judge;
    private String to;
    private List<String> scatters;
}
