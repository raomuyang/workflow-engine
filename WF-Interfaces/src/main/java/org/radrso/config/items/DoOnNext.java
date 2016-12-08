package org.radrso.config.items;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by raomengnan on 16-12-8.
 * step     步奏ID
 * call     org.radrso.xxx.Methodxxx 或者 url,可以调用某个方法或者直接请求某个url
 * method   CLASS / GET / POST / PUT / DELETE...
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DoOnNext {
    private int step;
    private String call;
    private String method;
    private Next next;
}
