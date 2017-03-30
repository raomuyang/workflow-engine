package org.radrso.workflow.exec.base;

import org.radrso.workflow.exec.BaseFlowActionsExecutor;
import org.radrso.workflow.resolvers.BaseWorkflowConfigResolver;
import rx.Subscriber;

/**
 * Created by raomengnan on 17-1-17.
 */

public class FlowActionSubscriber extends Subscriber<BaseWorkflowConfigResolver> {

    private BaseWorkflowConfigResolver workflowResolver;
    private BaseFlowActionsExecutor actionExecutor;

    public FlowActionSubscriber(BaseFlowActionsExecutor actionExecutor){
        this.actionExecutor = actionExecutor;
    }

    @Override
    public void onCompleted() {
        actionExecutor.doOnStepCompleted(workflowResolver);
    }

    @Override
    public void onError(Throwable throwable) {
        actionExecutor.doOnStepError(workflowResolver, throwable);
    }

    @Override
    public void onNext(BaseWorkflowConfigResolver workflowResolver) {
        this.workflowResolver = workflowResolver;
        actionExecutor.doNextStep(workflowResolver);
    }


}

