package org.radrso.workflow.wfservice.subscribe;

import org.radrso.workflow.entities.wf.WorkflowInstance;
import org.radrso.workflow.resolvers.WorkflowResolver;
import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created by raomengnan on 17-1-17.
 */
public class WorkflowObservable {
    public static void subscribe(StepAction stepAction, WorkflowResolver workflowResolver){
        workflowResolver.getWorkflowInstance().setStatus(WorkflowInstance.RUNNING);
        Observable.just(workflowResolver)
                .subscribeOn(Schedulers.io())
                .subscribe(new StepSubscriber(stepAction));
    }
}
