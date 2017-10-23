package org.radrso.workflow.handler;

import lombok.extern.log4j.Log4j;
import org.radrso.workflow.constant.EngineConstant;
import org.radrso.workflow.entities.exceptions.UnknownExceptionInRunning;
import org.radrso.workflow.entities.model.Next;
import org.radrso.workflow.entities.model.StepProcess;
import org.radrso.workflow.entities.model.WorkflowInstance2;
import org.radrso.workflow.entities.schema.WorkflowSchema;
import org.radrso.workflow.entities.schema.items.Step;
import org.radrso.workflow.entities.schema.items.Switch;
import org.radrso.workflow.entities.schema.items.Transfer;
import org.radrso.workflow.internal.function.Functions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Rao-Mengnan
 * on 2017/10/19.
 */
@Log4j
public class FlowStepHandler {
    private WorkflowSchema schema;
    private Map<String, Step> stepMap;

    public FlowStepHandler(WorkflowSchema schema) {
        this.schema = schema;
        this.stepMap = new ConcurrentHashMap<>();
        for (Step step : schema.getSteps()) {
            stepMap.put(step.getSign(), step);
        }
    }

    /**
     * 指向当前已完成步骤的游标，开始向下一步骤转移
     */
    public List<String> getCursor(WorkflowInstance2 instance) {
        List<String> cursor = instance.getCursor();
        if (cursor == null) {
            log.warn("Invalid cursor.");
            // TODO throw it
            return new ArrayList<>();
        }
        if (cursor.size() == 0) {
            log.info("Current cursor size 0.");
            cursor.add(EngineConstant.SCHEMA_START_SIGN);
        }
        return cursor;
    }

    private Step getLastStep(String sign) {

        Step step = stepMap.get(sign);
        if (step == null) {
            // TODO throw it
            log.warn("No such step: " + sign);
            return null;
        }
        return step;
    }

    public List<Next> transferTo(String cursor, WorkflowInstance2 instance) throws UnknownExceptionInRunning, Exception {

        Step lastStep = getLastStep(cursor);
        if (lastStep == null) return null;

        List<Next> nextList = new ArrayList<>();

        Transfer transfer = lastStep.getTransfer();
        List<Transfer> scatters = transfer.getScatters();

        Next next = getNext(transfer, cursor, instance);
        nextList.add(next);

        for (Transfer t : scatters) {
            if (t.getScatters().size() > 0) {
                scatters.addAll(t.getScatters());
            }
            Next concurrent = getNext(t, cursor, instance);
            nextList.add(concurrent);
        }

        return nextList;
    }

    /**
     *
     * @param transfer inner transfer schema body
     * @param instance workflow instance
     * @return Switch transfer direction.
     */
    private Transfer selectSwitch(Transfer transfer, WorkflowInstance2 instance) throws Exception {
        if (transfer.getRunSwitch() == null) {
            return transfer;
        }

        Switch switchBody = transfer.getRunSwitch();
        SchemaParamHandler paramsResolver = new SchemaParamHandler(instance);
        String type = switchBody.getType();

        Object variableA = paramsResolver.convertStrParam(
                String.valueOf(switchBody.getVariable()), type);

        Object compareTo = paramsResolver.convertStrParam(
                String.valueOf(switchBody.getCompareTo()), type);

        String condition = switchBody.getExpression();
        boolean result = Functions.condition(condition).check(variableA, compareTo);
        return result ? switchBody.getIfTransfer() : switchBody.getElseTransfer();
    }

    private Next getNext(Transfer transfer, String precursor, WorkflowInstance2 instance) throws UnknownExceptionInRunning, Exception {
        Next next = new Next();
        next.setPrecursor(precursor);

        Transfer toNext = selectSwitch(transfer, instance);
        String nextCursor = toNext.getTo();
        StepProcess stepProcess = instance.getStepProcessMap().get(nextCursor);
        stepProcess.setPreNode(precursor);

        next.setProcess(stepProcess);
        next.setTransfer(toNext);
        return next;
    }

}
