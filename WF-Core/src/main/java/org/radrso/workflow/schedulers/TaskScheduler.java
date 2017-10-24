package org.radrso.workflow.schedulers;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import lombok.extern.log4j.Log4j;
import org.radrso.workflow.internal.model.Next;
import org.radrso.workflow.internal.model.Tasks;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;

/**
 * Created by Rao-Mengnan
 * on 2017/10/19.
 */
@Log4j
public class TaskScheduler {

    private static final int DEFAULT_EXECUTOR_SIZE = 16;

    private static ListeningExecutorService executor;
    private static TaskScanner scanner;

    public static void submit(List<? extends Next> nextList) {
        List<TaskAgent> agents = new ArrayList<>();
        for (Next sub: nextList) agents.add(new TaskAgent(sub));
        Tasks.submit(agents);
    }

    synchronized static ListeningExecutorService getService() {
        if (executor == null) {
            executor = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(DEFAULT_EXECUTOR_SIZE));
        }

        if (scanner == null) {
            scanner = new TaskScanner();
            scanner.start();
        }

        try {
            executor.submit(() -> log.info("Activate the executor service"));
        } catch (RejectedExecutionException ignored) {
            // 预防启动后马上就使用"ctrl+c"杀死进程
        }
        return executor;
    }

    static class TaskScanner extends Thread {

    }
}
