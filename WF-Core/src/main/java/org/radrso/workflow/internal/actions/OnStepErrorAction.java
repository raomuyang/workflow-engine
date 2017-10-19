package org.radrso.workflow.internal.actions;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import lombok.extern.log4j.Log4j;
import org.bson.types.ObjectId;
import org.radrso.workflow.base.Commander;
import org.radrso.workflow.constant.ExceptionCode;
import org.radrso.workflow.entities.schema.items.Step;
import org.radrso.workflow.entities.exceptions.WFRuntimeException;
import org.radrso.workflow.entities.wf.StepStatus;
import org.radrso.workflow.entities.wf.WorkflowErrorLog;
import org.radrso.workflow.entities.wf.WorkflowInstance;
import org.radrso.workflow.resolvers.FlowResolver;

import java.util.Map;

/**
 * Created by rao-mengnan on 2017/5/18.
 */
@Log4j
public class OnStepErrorAction extends AbstractAction implements Consumer<Throwable>{
    private FlowResolver workflowResolver;

    public OnStepErrorAction(Commander workflowSynchronize, FlowResolver workflowResolver) {
        super(workflowSynchronize);
        this.workflowResolver = workflowResolver;
    }


    @Override
    public void accept(@NonNull Throwable throwable) throws Exception {
        WorkflowInstance instance = workflowResolver.getWorkflowInstance();
        log.error("[STEP-EXCEPTION] " + instance.getInstanceId() + " " + workflowResolver.getCurrentStep().getSign() + " " + throwable);
        if (WFRuntimeException.WORKFLOW_EXPIRED.equals(throwable.getMessage()))
            instance.setStatus(WorkflowInstance.EXPIRED);
        else
            instance.setStatus(WorkflowInstance.EXCEPTION);

        Step currentStep = workflowResolver.getCurrentStep();
        if (currentStep != null) {
            String stepSign = workflowResolver.getCurrentStep().getSign();
            Map<String, StepStatus> stepStatusMap = workflowResolver.getWorkflowInstance().getStepStatusesMap();
            StepStatus stepStatus = stepStatusMap.get(stepSign);

            workflowResolver.getWorkflowInstance().getStepProcess().put(stepSign, Step.STOPPED);
            if (stepStatus != null)
                stepStatus.setStatus(Step.STOPPED);
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
    public Action setResolver(FlowResolver resolver) {
        this.workflowResolver = resolver;
        return this;
    }
}
