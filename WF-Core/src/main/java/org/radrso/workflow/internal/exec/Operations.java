package org.radrso.workflow.internal.exec;

import lombok.extern.log4j.Log4j;
import org.radrso.plugins.requests.entity.ResponseCode;
import org.radrso.workflow.base.Commander;
import org.radrso.workflow.entities.schema.items.Transfer;
import org.radrso.workflow.entities.exceptions.WFRuntimeException;
import org.radrso.workflow.entities.info.WorkflowExecuteStatus;
import org.radrso.workflow.entities.info.WorkflowInstance;
import org.radrso.workflow.resolvers.FlowResolver;

import java.util.Date;

/**
 * Created by rao-mengnan on 2017/5/18.
 */

@Log4j
public class Operations {
    private Commander commander;

    public Operations(Commander workflowSynchronize) {
        this.commander = workflowSynchronize;
    }


    public void verifyDate(FlowResolver workflowResolver) {
        Transfer lastTransfer = workflowResolver.getCurrentTransfer();
        Date diedline = null;
        boolean isContinue = true;
        if (lastTransfer != null && (diedline = lastTransfer.getDeadline()) != null)
            isContinue = new Date().before(diedline);

        isContinue = isContinue && checkWorkflowStatus(workflowResolver);
        if (!isContinue)
            throw new WFRuntimeException(WFRuntimeException.WORKFLOW_EXPIRED, ResponseCode.HTTP_FORBIDDEN.code());

    }


    public boolean checkWorkflowStatus(FlowResolver workflowResolver) {
        String status = commander.getWorkflowStatus(workflowResolver.getWorkflowInstance().getWorkflowId());
        if (status == null)
            throw new WFRuntimeException(WFRuntimeException.NO_SUCH_WORKFLOW_STATUS, ResponseCode.HTTP_NOT_FOUND.code());

        boolean available = WorkflowExecuteStatus.START.equals(status);
        if (!available)
            return false;
        return true;
    }

    /**
     * 同步instance状态，检查instance是否已经被中断
     *
     * @param instanceId
     * @return
     */
    public boolean checkIsInstanceInterrupted(String instanceId) {
        String mainInstance = instanceId;
        if (mainInstance.contains("-")) {
            mainInstance = mainInstance.substring(0, mainInstance.indexOf("-"));
        }

        WorkflowInstance instance = commander.getInstance(mainInstance);
        if (instance == null) {
            String msg = String.format("Check instance stopped: Can't find instance by [%s]", instanceId);
            log.error(msg);
            throw new WFRuntimeException(msg, ResponseCode.HTTP_NOT_FOUND.code());
        }

        if (instance.getStatus().equals(WorkflowInstance.INTERRUPTED)) {
            return true;
        }
        return false;
    }

    public boolean interruptInstanceProcess(String instanceId) {
        log.info("[INTERRUPT] " + instanceId);
        if (instanceId.contains("-")) {
            instanceId = instanceId.substring(0, instanceId.indexOf("-"));
        }
        WorkflowInstance instance = commander.getInstance(instanceId);
        if (instance == null){
            return false;
        }

        instance.setStatus(WorkflowInstance.INTERRUPTED);
        return commander.updateInstance(instance);
    }
}
