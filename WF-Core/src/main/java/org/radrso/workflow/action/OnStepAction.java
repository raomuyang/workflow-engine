package org.radrso.workflow.action;

import org.radrso.workflow.constant.WFStatusCode;
import org.radrso.workflow.entity.StatusEnum;
import org.radrso.workflow.entity.exception.WFException;
import org.radrso.workflow.entity.model.StepProgress;
import org.radrso.workflow.internal.model.Next;
import org.radrso.workflow.entity.model.WorkflowResult;
import org.radrso.workflow.entity.schema.items.Step;
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
