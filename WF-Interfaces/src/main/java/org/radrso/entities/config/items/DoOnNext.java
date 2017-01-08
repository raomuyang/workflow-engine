package org.radrso.entities.config.items;

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
    public static final String WAIT = "wait";
    public static final String RUNNING = "running";
    public static final String FINISHED = "finished";
    private int step;
    private String name;
    private Task task;
    private String call;
    private String method;
    private InputItem[] input;
    private Next next;
    private String status = WAIT;
}
