package org.radrso.workflow.actions;

import org.radrso.workflow.constant.WFStatusCode;
import org.radrso.workflow.entities.StatusEnum;
import org.radrso.workflow.entities.exceptions.WFException;
import org.radrso.workflow.entities.model.StepProgress;
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

        StepProgress progress = next.getProgress();
        List<Map<String, Object>> params = next.getParams();
        RequestHandler requestHandler = new RequestHandler(step, params);
        WorkflowResult response = requestHandler.handle();

        progress.setResult(response);
        if (WFStatusCode.isOK(response.getCode())) {
            progress.setStatus(StatusEnum.COMPLETED);
        } else {
            throw new WFException(response.getCode(), response.getMsg());
        }
    }
}
