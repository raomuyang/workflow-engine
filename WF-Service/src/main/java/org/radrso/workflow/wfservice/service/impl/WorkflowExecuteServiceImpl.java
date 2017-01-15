package org.radrso.workflow.wfservice.service.impl;

import lombok.extern.java.Log;
import org.radrso.workflow.rmi.WorkflowInstanceExecutor;
import org.radrso.workflow.resolvers.WorkflowResolver;
import org.radrso.workflow.entities.config.items.Step;
import org.radrso.workflow.entities.exceptions.ConfigReadException;
import org.radrso.workflow.entities.response.WFResponse;
import org.radrso.workflow.wfservice.service.WorkflowExecuteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by raomengnan on 17-1-14.
 */
@Service
@Log
public class WorkflowExecuteServiceImpl implements WorkflowExecuteService {

    @Autowired
    WorkflowInstanceExecutor workflowInstanceExecutor;

    @Override
    public WFResponse execute(WorkflowResolver workflowResolver) {
        Observable<WorkflowResolver> observable = Observable.just(workflowResolver);

        do {
            observable = observable.doOnNext(new Action1<WorkflowResolver>() {
                @Override
                public void call(WorkflowResolver workflowResolver) {
                    try {

                        int wait = 0;
                        if (workflowResolver.getCurrentTransfer() != null)
                            wait = workflowResolver.getCurrentTransfer().getWait();

                        workflowResolver.next();

                        try {
                            Thread.sleep(wait * 60 * 1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        Step step = workflowResolver.getCurrentStep();
                        Object[] params = workflowResolver.getCurrentStepParams();
                        String[] paramNames = workflowResolver.getCurrentStepParamNames();

                        WFResponse response = null;
                        if (step.getCall() != null)
                            response = workflowInstanceExecutor.execute(step, params, paramNames);

                    } catch (ConfigReadException e) {
                        e.printStackTrace();
                    }
                }
            });
        }while (!workflowResolver.eof());

        observable.subscribeOn(Schedulers.io()).observeOn(Schedulers.io()).subscribe(new Subscriber<WorkflowResolver>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onNext(WorkflowResolver workflowResolver) {

            }
        });
        return null;
    }
}
