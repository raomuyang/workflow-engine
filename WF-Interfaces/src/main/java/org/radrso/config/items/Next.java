package org.radrso.config.items;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by raomengnan on 16-12-8.
 * 用于组成“下一步”和一些“判断条件”
 * conditionItems   判断条件的数组
 * nextStep         下一步，break到output，则nextStep=0
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Next {
    private ConditionItem[] conditionItems;
    private int nextStep = 0;
}
