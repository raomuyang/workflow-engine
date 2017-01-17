package org.radrso.workflow.wfservice.subscribe;

import org.radrso.workflow.entities.config.items.Step;
import org.radrso.workflow.resolvers.WorkflowResolver;
import rx.Subscriber;

/**
 * Created by raomengnan on 17-1-17.
 */

public class StepSubscriber extends Subscriber<WorkflowResolver> {

    private WorkflowResolver workflowResolver;
    private StepAction stepAction;

    public StepSubscriber(StepAction action){
        this.stepAction = action;
    }

    @Override
    public void onCompleted() {
        stepAction.stepCompleted(workflowResolver);
        if(!workflowResolver.eof()) {
            WorkflowObservable.subscribe(stepAction, workflowResolver);
            workflowResolver.getWorkflowInstance().getStepProcess().put(WorkflowResolver.FINISH, Step.FINISHED);
        }
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

