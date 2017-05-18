package org.radrso.workflow.internal.exec;

import io.reactivex.Observable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import lombok.extern.log4j.Log4j;
import org.radrso.workflow.exec.FlowExecutor;
import org.radrso.workflow.base.Commander;
import org.radrso.workflow.internal.exec.actions.*;
import org.radrso.workflow.resolvers.FlowResolver;

/**
 * 流程自动执行器
 * Created by rao-mengnan on 2017/3/29.
 */
@Log4j
public class FlowExecutorImpl extends FlowExecutor {
    private Commander commander;

    public FlowExecutorImpl(Commander commander) {
        this.commander = commander;
    }

    @Override
    public void start(FlowResolver flowResolver) {
        Actions.startStream(flowResolver, commander);
    }


    @Override
    public boolean interrupt(String instanceId) {
        return new Operations(commander).interruptInstanceProcess(instanceId);
    }

    @Override
    public void restart(FlowResolver workflowResolver) {
        log.info("[RESTARTING] ---------- " + workflowResolver.getWorkflowInstance().getInstanceId());
        Consumer<FlowResolver> interruptAndCheckAction
                = (Consumer<FlowResolver>) Actions.getAction(ActionEnum.INTERRUPT_AND_CHECK, commander);
        Action executeNextAction
                = (Action) Actions.getAction(ActionEnum.ON_EXEC_NEXT, commander)
                .setResolver(workflowResolver);
        Observable.just(workflowResolver)
                .doOnNext(interruptAndCheckAction)
                .doOnComplete(executeNextAction)
                .subscribeOn(Schedulers.io()).subscribe();
    }

}
