package org.radrso.entities.config.items;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

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
public class Judge {
    private String compute;
    private String computeWith;
    private String expression;
    private String next;
    private Transfer passTransfer;
    private Transfer nopassTransfer;
}
