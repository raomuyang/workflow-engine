package org.radrso.config.items;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by raomengnan on 16-12-8.
 * compare  要比较的对象
 * judge    判断条件  >  =  <  >=  <=
 * next     if true, next
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConditionItem {
    private String compare;
    private String judge;
    private String next;
}
