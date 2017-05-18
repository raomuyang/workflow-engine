package org.radrso.workflow.internal.exec.actions;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import lombok.extern.log4j.Log4j;
import org.radrso.workflow.base.Commander;
import org.radrso.workflow.entities.config.items.Step;
import org.radrso.workflow.entities.wf.StepStatus;
import org.radrso.workflow.entities.wf.WorkflowInstance;
import org.radrso.workflow.resolvers.FlowResolver;

import java.util.Date;
import java.util.Map;

/**
 * Created by rao-mengnan on 2017/5/18.
 */
@Log4j
public class OnStepCompletedAction extends AbstractAction implements Consumer<FlowResolver>{
    public OnStepCompletedAction(Commander workflowSynchronize) {
        super(workflowSynchronize);
    }

    @Override
    public void accept(@NonNull FlowResolver workflowResolver) throws Exception {
        String instanceId = workflowResolver.getWorkflowInstance().getInstanceId();
        String stepName = workflowResolver.getCurrentStep().getName();

        log.info("[STEP-COMPLETED] " + instanceId + " " + stepName);

        // 只有在instance没有被中断的情况下才能继续执行下一步
        boolean stopped = operations.checkIsInstanceInterrupted(workflowResolver.getWorkflowInstance().getInstanceId());
        if (stopped){
            log.info(String.format("[STEP-INTERRUPT] Instance %s is interrupted, (%s) status will discard", instanceId, stepName));
            WorkflowInstance originInfo = commander.getInstance(instanceId);
            if (originInfo == null){
                log.warn(String.format("Check instance stopped: Can't find instance by [%s], maybe doesn't created", instanceId));
                return;
            }
            originInfo.setStatus(WorkflowInstance.INTERRUPTED);
            commander.updateInstance(originInfo);
            return;
        }

        String stepSign = workflowResolver.getCurrentStep().getSign();
        workflowResolver.getWorkflowInstance().getStepProcess().put(stepSign, Step.FINISHED);

        Map<String, StepStatus> stepStatusMap = workflowResolver.getWorkflowInstance().getStepStatusesMap();
        stepStatusMap.get(stepSign).setStatus(Step.FINISHED);
        workflowResolver.getWorkflowInstance().getFinishedSequence().add(
                workflowResolver.getCurrentStep().getSign()
        );

        boolean eof = workflowResolver.eof();
        if (eof) {
            workflowResolver.getWorkflowInstance().setStatus(WorkflowInstance.COMPLETED);
            workflowResolver.getWorkflowInstance().setSubmitTime(new Date());
        }

        commander.updateInstance(workflowResolver.getWorkflowInstance());

        if (!eof) {
            Actions.startStream(workflowResolver, commander);
        }
    }

    @Override
    public Action setResolver(FlowResolver resolver) {
        return this;
    }
}
