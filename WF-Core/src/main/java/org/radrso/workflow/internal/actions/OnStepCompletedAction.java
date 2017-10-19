package org.radrso.workflow.internal.actions;

import io.reactivex.functions.Action;
import lombok.extern.log4j.Log4j;
import org.radrso.workflow.base.Commander;
import org.radrso.workflow.entities.schema.items.Step;
import org.radrso.workflow.entities.info.StepStatus;
import org.radrso.workflow.entities.info.WorkflowInstance;
import org.radrso.workflow.resolvers.FlowResolver;

import java.util.Date;
import java.util.Map;

/**
 * Created by rao-mengnan on 2017/5/18.
 */
@Log4j
public class OnStepCompletedAction extends AbstractAction implements Action{
    private FlowResolver flowResolver;

    public OnStepCompletedAction(Commander workflowSynchronize) {
        super(workflowSynchronize);
    }


    @Override
    public void run() throws Exception {
        String instanceId = flowResolver.getWorkflowInstance().getInstanceId();
        String stepName = flowResolver.getCurrentStep().getName();

        log.info("[STEP-COMPLETED] " + instanceId + " " + stepName);

        // 只有在instance没有被中断的情况下才能继续执行下一步
        boolean stopped = operations.checkIsInstanceInterrupted(flowResolver.getWorkflowInstance().getInstanceId());
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

        String stepSign = flowResolver.getCurrentStep().getSign();
        flowResolver.getWorkflowInstance().getStepProcess().put(stepSign, Step.FINISHED);

        Map<String, StepStatus> stepStatusMap = flowResolver.getWorkflowInstance().getStepStatusesMap();
        stepStatusMap.get(stepSign).setStatus(Step.FINISHED);
        flowResolver.getWorkflowInstance().getFinishedSequence().add(
                flowResolver.getCurrentStep().getSign()
        );

        boolean eof = flowResolver.eof();
        if (eof) {
            flowResolver.getWorkflowInstance().setStatus(WorkflowInstance.COMPLETED);
            flowResolver.getWorkflowInstance().setSubmitTime(new Date());
        }

        commander.updateInstance(flowResolver.getWorkflowInstance());

        if (!eof) {
            Actions.startStream(flowResolver, commander);
        }
    }

    @Override
    public AbstractAction setResolver(FlowResolver resolver) {
        this.flowResolver = resolver;
        return this;
    }

}
