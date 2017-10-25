package org.radrso.workflow.handler;

import lombok.NonNull;
import lombok.extern.log4j.Log4j;
import org.radrso.workflow.constant.EngineConstant;
import org.radrso.workflow.constant.WFStatusCode;
import org.radrso.workflow.entities.exceptions.WFError;
import org.radrso.workflow.entities.exceptions.WFException;
import org.radrso.workflow.internal.model.Next;
import org.radrso.workflow.entities.model.StepProgress;
import org.radrso.workflow.internal.model.WorkflowInstanceInfo;
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
    private Map<String, Step> stepMap;

    public FlowStepHandler(WorkflowSchema schema) {
        this.stepMap = new ConcurrentHashMap<>();
        for (Step step : schema.getSteps()) {
            stepMap.put(step.getSign(), step);
        }
    }

    /**
     * 指向当前已完成步骤的游标，开始向下一步骤转移
     */
    public List<String> getCursor(WorkflowInstanceInfo instanceInfo) {
        List<String> cursor = instanceInfo.getCursor();
        if (cursor == null) {
            log.warn("Invalid cursor.");
            throw new WFException("Cursor is null.");
        }
        if (cursor.size() == 0) {
            log.info("Current cursor size 0.");
            cursor.add(EngineConstant.SCHEMA_START_SIGN);
        }
        return cursor;
    }

    public Step getStepInfo(@NonNull String cursor) {
        return stepMap.get(cursor);
    }

    public List<Next> transferTo(String cursor, WorkflowInstanceInfo instance) throws WFException {

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
     * @param instanceInfo workflow instanceInfo
     * @return Switch transfer direction.
     */
    Transfer selectSwitch(Transfer transfer, WorkflowInstanceInfo instanceInfo) throws WFException {
        if (transfer.getRunSwitch() == null) {
            return transfer;
        }

        Switch switchBody = transfer.getRunSwitch();
        String type = switchBody.getType();

        try {
            Object variableA = Functions.mapParam2(instanceInfo).mapTo(String.valueOf(switchBody.getVariable()), type);

            Object compareTo =Functions.mapParam2(instanceInfo).mapTo(String.valueOf(switchBody.getCompareTo()), type);

            String condition = switchBody.getExpression();
            boolean result = Functions.condition(condition).check(variableA, compareTo);
            return result ? switchBody.getThanTransfer() : switchBody.getElseTransfer();
        } catch (Exception e) {
            if (e instanceof WFError) {
                WFError e1 = (WFError) e;
                throw new WFException(e1.getCode(), e1.getDetailMessage(), e);
            }
            throw new WFException(WFStatusCode.SCHEMA_PARSE_ERROR.code(), e.getMessage(), e);
        }
    }

    /**
     * TODO 目前step process在此处初始化，尚未考虑到从持久数据中初始化
     * init step progress, step info, step params
     * @param transfer the next transfer info.
     * @param precursor the precursor of next node.
     * @param instanceInfo information of this workflow runtime instance.
     * @return Next node, include the next step info, the step params...
     * @throws WFException Schema resolve failed.
     */
    Next getNext(Transfer transfer, String precursor, WorkflowInstanceInfo instanceInfo) throws WFException {
        Next next = new Next();
        next.setPrecursor(precursor);

        // init step progress
        Transfer toNext = selectSwitch(transfer, instanceInfo);
        String nextCursor = toNext.getTo();
        StepProgress stepProgress = instanceInfo.getStepProgressMap().get(nextCursor);
        Step stepInfo = stepMap.get(nextCursor);

        if (stepProgress == null) {
            stepProgress = new StepProgress(instanceInfo.getInstanceId(), nextCursor, stepInfo.getName());
            instanceInfo.getStepProgressMap().put(nextCursor, stepProgress);
        }
        stepProgress.setPrecursor(precursor);

        try {
            List<Map<String, Object>> params = Functions.mapParam1(instanceInfo).mapTo(transfer);
            stepProgress.setParams(params);

            next.setParams(params);
            next.setProgress(stepProgress);
            next.setStepInfo(stepInfo);
            return next;
        } catch (Exception e) {
            if (e instanceof WFError) {
                WFError e1 = (WFError) e;
                throw new WFException(e1.getCode(), e1.getDetailMessage(), e);
            }
            throw new WFException(WFStatusCode.SCHEMA_PARSE_ERROR.code(), e.getMessage(), e);
        }
    }

    Step getLastStep(String sign) {

        Step step = stepMap.get(sign);
        if (step == null) {
            log.warn("No such step: " + sign);
            throw new WFException("No such step: " + sign);
        }
        return step;
    }

}
