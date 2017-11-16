package org.radrso.workflow.entity.schema.items;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 * Created by raomengnan on 16-12-8.
 * step     步骤的标志（ID）
 * call     调用类方法如org.radrso.xxx.Methodxxx 或者 url（调用某个方法或者直接请求某个url）
 * method   CLASS （表示调用类方法）/ GET / POST / PUT / DELETE...（表示调用Http请求）
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Step implements Serializable{
    public static final String WAIT = "wait";
    public static final String RUNNING = "running";
    public static final String FINISHED = "finished";
    public static final String STOPPED = "stopped";

    private String sign;
    private String name;
    private String call;
    private String method;
    private Transfer transfer;
}
