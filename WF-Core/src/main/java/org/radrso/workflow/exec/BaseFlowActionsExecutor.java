package org.radrso.workflow.exec;

import org.radrso.workflow.entities.wf.WorkflowInstance;
import org.radrso.workflow.exec.base.FlowActionSubscriber;
import org.radrso.workflow.resolvers.BaseWorkflowConfigResolver;
import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created by raomengnan on 17-1-17.
 * 递归遍历所有的步骤节点
 * 自动执行工作流
 */
public abstract class BaseFlowActionsExecutor {
    public void execute(BaseWorkflowConfigResolver workflowResolver){
        workflowResolver.getWorkflowInstance().setStatus(WorkflowInstance.RUNNING);
        Observable.just(workflowResolver)
                .subscribeOn(Schedulers.io())
                .subscribe(new FlowActionSubscriber(this));
    }

    public abstract void doNextStep(BaseWorkflowConfigResolver workflowResolver);
    public abstract void doOnStepError(BaseWorkflowConfigResolver workflowResolver, Throwable throwable);
    public abstract void doOnStepCompleted(BaseWorkflowConfigResolver workflowResolver);
    public abstract boolean interruptInstanceExec(String instanceId);
    public abstract void restart(final BaseWorkflowConfigResolver workflowResolver);
}
