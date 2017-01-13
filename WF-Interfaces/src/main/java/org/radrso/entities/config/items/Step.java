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
public class Step {
    public static final String WAIT = "wait";
    public static final String RUNNING = "running";
    public static final String FINISHED = "finished";
    private String sign;
    private String name;
    private String call;
    private String method;
    private int loop = 1;
    private Transfer transfer;
    private String status = WAIT;
}
