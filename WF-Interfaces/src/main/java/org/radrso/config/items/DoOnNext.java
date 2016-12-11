package org.radrso.config.items;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Created by raomengnan on 16-12-8.
 * step     步奏ID
 * call     org.radrso.xxx.Methodxxx 或者 url,可以调用某个方法或者直接请求某个url
 * method   CLASS / GET / POST / PUT / DELETE...
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class DoOnNext {
    private int step;
    private String name;
    private String call;
    private String method;
    private InputItem[] input;
    private Next next;
}
