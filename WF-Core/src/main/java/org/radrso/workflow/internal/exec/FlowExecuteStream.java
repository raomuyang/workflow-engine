package org.radrso.workflow.internal.exec;

import io.reactivex.Observable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import org.radrso.workflow.base.Commander;
import org.radrso.workflow.entities.info.WorkflowInstance;
import org.radrso.workflow.internal.actions.ActionEnum;
import org.radrso.workflow.internal.actions.Actions;
import org.radrso.workflow.resolvers.FlowResolver;

/**
 * Created by rao-mengnan on 2017/5/18.
 */
public class FlowExecuteStream {
    private FlowResolver resolver;
    private Commander commander;

    public FlowExecuteStream(FlowResolver resolver, Commander commander) {
        this.resolver = resolver;
        this.commander = commander;
    }

    public void process() {
        resolver.getWorkflowInstance().setStatus(WorkflowInstance.RUNNING);

        Consumer<FlowResolver> onStepExecAction
                = (Consumer<FlowResolver>) Actions.getAction(ActionEnum.ON_STEP_EXEC, commander);
        Action onStepCompleted
                = (Action) Actions.getAction(ActionEnum.ON_STEP_COMPLETED, commander).setResolver(resolver);
        Consumer<Throwable> onStepError
                = (Consumer<Throwable>) Actions.getAction(ActionEnum.ON_STEP_ERROR, commander).setResolver(resolver);

        Observable.just(resolver)
                .doOnNext(onStepExecAction)
                .doOnComplete(onStepCompleted)
                .doOnError(onStepError)
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    public void restartProcess() {
        Consumer<FlowResolver> interruptAndCheckAction
                = (Consumer<FlowResolver>) Actions.getAction(ActionEnum.INTERRUPT_AND_CHECK, commander);
        Action executeNextAction
                = (Action) Actions.getAction(ActionEnum.ON_EXEC_NEXT, commander)
                .setResolver(resolver);
        Observable.just(resolver)
                .doOnNext(interruptAndCheckAction)
                .doOnComplete(executeNextAction)
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

}
