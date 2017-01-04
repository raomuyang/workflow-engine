package org.radrso.entities.config.items;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

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
public class Next {
    private ConditionItem[] conditions;
    private int nextStep = 0;
}
