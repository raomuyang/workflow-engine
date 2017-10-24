package org.radrso.workflow.schedulers;

import org.radrso.workflow.internal.model.Next;

import java.io.Serializable;

/**
 * Created by Rao-Mengnan
 * on 2017/10/24.
 */
public class TaskAgent implements Serializable {

    private Next subject;
    private long timestamp;

    public TaskAgent(Next subject) {
        timestamp = System.currentTimeMillis();
        this.subject = subject;
    }

    public Next getSubject() {
        return subject;
    }
}
