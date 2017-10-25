package org.radrso.workflow.action;

import org.radrso.workflow.constant.WFStatusCode;
import org.radrso.workflow.entity.exception.WFException;
import org.radrso.workflow.function.Consumer;
import org.radrso.workflow.internal.model.Next;

import java.util.Date;

/**
 * Created by Rao-Mengnan
 * on 2017/10/24.
 */
public class BeforeStepStart implements Consumer<Next> {
    @Override
    public void accept(Next next) throws Exception {
        Date deadline = next.getDeadline();
        if (deadline.after(new Date())) {
            String msg = String.format("%s - %s", WFStatusCode.INTERRUPT_EXCEPTION.info(), next.getProgress().getInstanceId());
            throw new WFException(WFStatusCode.INTERRUPT_EXCEPTION.code(), msg);
        }



    }
}
