package org.radrso.workflow.internal.exec;

import io.reactivex.Observable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import org.radrso.workflow.base.Commander;
import org.radrso.workflow.entities.wf.WorkflowInstance;
import org.radrso.workflow.internal.exec.actions.ActionEnum;
import org.radrso.workflow.internal.exec.actions.Actions;
import org.radrso.workflow.resolvers.FlowResolver;

/**
 * Created by rao-mengnan on 2017/5/18.
 */
public class FlowExecuteStream {
    private FlowResolver resolver;
    private Commander workflowSynchronize;

    public FlowExecuteStream(FlowResolver resolver, Commander workflowSynchronize) {
        this.resolver = resolver;
        this.workflowSynchronize = workflowSynchronize;
    }

    public void process() {
        resolver.getWorkflowInstance().setStatus(WorkflowInstance.RUNNING);

        Consumer<FlowResolver> onStepExecAction
                = (Consumer<FlowResolver>) Actions.getAction(ActionEnum.ON_STEP_EXEC, workflowSynchronize);
        Action onStepCompleted
                = (Action) Actions.getAction(ActionEnum.ON_STEP_COMPLETED, workflowSynchronize).setResolver(resolver);
        Consumer<Throwable> onStepError
                = (Consumer<Throwable>) Actions.getAction(ActionEnum.ON_STEP_ERROR, workflowSynchronize).setResolver(resolver);

        Observable.just(resolver)
                .doOnNext(onStepExecAction)
                .doOnComplete(onStepCompleted)
                .doOnError(onStepError)
                .observeOn(Schedulers.io())
                .subscribe();
    }

}
