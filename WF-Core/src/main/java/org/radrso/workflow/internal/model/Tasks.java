package org.radrso.workflow.internal.model;

import lombok.NonNull;
import org.radrso.workflow.scheduler.TaskAgent;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by Rao-Mengnan
 * on 2017/10/24.
 */
public class Tasks {
    private static Tasks tasks;
    private ConcurrentLinkedQueue<TaskAgent> taskQueue;

    private Tasks() {
    }

    static {
        tasks = new Tasks();
    }

    public static Tasks get() {
        return tasks;
    }

    public static void submit(@NonNull List<TaskAgent> taskAgents) {
        if (taskAgents.size() == 0) return;
        tasks.taskQueue.addAll(taskAgents);
    }

}
