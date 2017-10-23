package org.radrso.workflow.action;

import io.reactivex.Observable;
import org.radrso.plugins.requests.entity.ResponseCode;
import org.radrso.workflow.entities.model.WorkflowInstance2;
import org.radrso.workflow.entities.model.WorkflowResult;
import org.radrso.workflow.entities.schema.items.Step;
import org.radrso.workflow.entities.schema.items.Transfer;
import org.radrso.workflow.function.Action;
import org.radrso.workflow.handler.FlowStepHandler;
import org.radrso.workflow.handler.RequestHandler;
import org.radrso.workflow.internal.function.Functions;

import java.util.List;
import java.util.Map;

/**
 * Created by Rao-Mengnan
 * on 2017/10/23.
 */
public class StepAction implements Action {
    private Transfer transfer;
    private WorkflowInstance2 instance;
    private FlowStepHandler handler;

    public StepAction(Transfer transfer, FlowStepHandler handler, WorkflowInstance2 instance) {
        this.transfer = transfer;
        this.instance = instance;
        this.handler = handler;
    }

    @Override
    public void run() {
        try {
            List<Map<String, Object>> params = Functions.mapParam1(instance).mapTo(transfer);
            Step step = handler.getStepInfo(transfer.getTo());
            WorkflowResult response;
            RequestHandler requestHandler = new RequestHandler(step, params);
            if (step.getCall() == null || step.getCall().indexOf(":") < 0) {
                response = new WorkflowResult(ResponseCode.HTTP_BAD_REQUEST.code(), "Error Protocol:" + step.getCall(), null);
            }
            String protocol = step.getCall().substring(0, step.getCall().indexOf(":"));


            if (protocol.toLowerCase().contains("http"))
                response = requestHandler.netRequest();
            else
                response = requestHandler.classRequest();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
