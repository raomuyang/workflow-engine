package org.radrso.workflow.actions;

import org.radrso.plugins.requests.entity.ResponseCode;
import org.radrso.workflow.constant.WFStatusCode;
import org.radrso.workflow.entities.StatusEnum;
import org.radrso.workflow.entities.exceptions.WFException;
import org.radrso.workflow.entities.model.StepProcess;
import org.radrso.workflow.internal.model.Next;
import org.radrso.workflow.entities.model.WorkflowResult;
import org.radrso.workflow.entities.schema.items.Step;
import org.radrso.workflow.function.Consumer;
import org.radrso.workflow.handler.RequestHandler;

import java.util.List;
import java.util.Map;

/**
 * Created by Rao-Mengnan
 * on 2017/10/23.
 */
public class OnStepAction implements Consumer<Next> {

    @Override
    public void accept(Next next) throws Exception {
        Step step = next.getStepInfo();

        StepProcess process = next.getProcess();
        List<Map<String, Object>> params = next.getParams();
        WorkflowResult response;
        RequestHandler requestHandler = new RequestHandler(step, params);
        if (step.getCall() == null || !step.getCall().contains(":")) {
            response = new WorkflowResult(ResponseCode.HTTP_BAD_REQUEST.code(), "Error Protocol:" + step.getCall(), null);
        } else {
            String protocol = step.getCall().substring(0, step.getCall().indexOf(":"));

            if (protocol.toLowerCase().contains("http"))
                response = requestHandler.netRequest();
            else
                response = requestHandler.classRequest();

        }
        process.setResult(response);
        if (WFStatusCode.isOK(response.getCode())) {
            process.setStatus(StatusEnum.COMPLETED);
        } else {
            throw new WFException(response.getCode(), response.getMsg());
        }
    }
}
