package org.radrso.workflow.exec;

import org.radrso.workflow.entities.wf.WorkflowInstance;
import org.radrso.workflow.exec.base.BaseStepAction;
import org.radrso.workflow.exec.base.StepSubscriber;
import org.radrso.workflow.resolvers.WorkflowResolver;
import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created by raomengnan on 17-1-17.
 */
public class StepExecutor {
    public static void execute(BaseStepAction stepAction, WorkflowResolver workflowResolver){
        workflowResolver.getWorkflowInstance().setStatus(WorkflowInstance.RUNNING);
        Observable.just(workflowResolver)
                .subscribeOn(Schedulers.io())
                .subscribe(new StepSubscriber(stepAction));
    }
}
