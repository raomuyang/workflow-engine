package org.radrso.workflow.actions;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import lombok.extern.log4j.Log4j;
import org.bson.types.ObjectId;
import org.radrso.workflow.base.Commander;
import org.radrso.workflow.constant.ExceptionCode;
import org.radrso.workflow.entities.StatusEnum;
import org.radrso.workflow.entities.schema.items.Step;
import org.radrso.workflow.entities.exceptions.WFRuntimeException;
import org.radrso.workflow.entities.model.StepProcess;
import org.radrso.workflow.entities.model.WorkflowErrorLog;
import org.radrso.workflow.entities.model.WorkflowInstance;
import org.radrso.workflow.resolvers.WorkflowResolver;

import java.util.Map;

/**
 * Created by rao-mengnan on 2017/5/18.
 */
@Log4j
public class OnStepErrorAction extends AbstractAction implements Consumer<Throwable>{
    private WorkflowResolver workflowResolver;

    public OnStepErrorAction(Commander workflowSynchronize, WorkflowResolver workflowResolver) {
        super(workflowSynchronize);
        this.workflowResolver = workflowResolver;
    }


    @Override
    public void accept(@NonNull Throwable throwable) throws Exception {
        WorkflowInstance instance = workflowResolver.getWorkflowInstance();
        log.error("[STEP-EXCEPTION] " + instance.getInstanceId() + " " + workflowResolver.getCurrentStep().getSign() + " " + throwable);
        if (WFRuntimeException.WORKFLOW_EXPIRED.equals(throwable.getMessage()))
            instance.setStatus(StatusEnum.EXPIRED);
        else
            instance.setStatus(StatusEnum.EXCEPTION);

        Step currentStep = workflowResolver.getCurrentStep();
        if (currentStep != null) {
            String stepSign = workflowResolver.getCurrentStep().getSign();
            Map<String, StepProcess> stepStatusMap = workflowResolver.getWorkflowInstance().getStepStatusesMap();
            StepProcess stepProcess = stepStatusMap.get(stepSign);

            workflowResolver.getWorkflowInstance().getStepProcess().put(stepSign, Step.STOPPED);
            if (stepProcess != null)
                stepProcess.setStatus(Step.STOPPED);
            else
                log.error("StepStatus is null:" + stepSign);
        }
        commander.updateInstance(instance);

        ObjectId objectId = new ObjectId();
        String msg = throwable.getMessage();
        if (msg == null || msg.equals(""))
            msg = throwable.toString();
        int code = ExceptionCode.UNKNOW.code();
        if (WFRuntimeException.class.isInstance(throwable))
            code = ((WFRuntimeException) throwable).getCode();
        WorkflowErrorLog errorLog = new WorkflowErrorLog(
                objectId.toHexString(),
                code,
                instance.getWorkflowId(),
                instance.getInstanceId(),
                workflowResolver.getCurrentStep().getSign(),
                msg,
                objectId.getDate(),
                throwable);
        commander.saveErrorLog(errorLog);
    }

    @Override
    public Action setResolver(WorkflowResolver resolver) {
        this.workflowResolver = resolver;
        return this;
    }
}
