package org.radrso.workflow.exec.base;

import org.radrso.workflow.resolvers.WorkflowResolver;
import rx.Subscriber;

/**
 * Created by raomengnan on 17-1-17.
 */

public class StepSubscriber extends Subscriber<WorkflowResolver> {

    private WorkflowResolver workflowResolver;
    private BaseStepAction stepAction;

    public StepSubscriber(BaseStepAction action){
        this.stepAction = action;
    }

    @Override
    public void onCompleted() {
        stepAction.stepCompleted(workflowResolver);
    }

    @Override
    public void onError(Throwable throwable) {
        stepAction.stepError(workflowResolver, throwable);
    }

    @Override
    public void onNext(WorkflowResolver workflowResolver) {
        this.workflowResolver = workflowResolver;
        stepAction.stepNext(workflowResolver);
    }


}

