package org.radrso.workflow.action;

import org.radrso.workflow.function.Consumer;

/**
 * Created by Rao-Mengnan
 * on 2017/10/23.
 */
public class OnStepError implements Consumer<Throwable> {

    @Override
    public void accept(Throwable t) throws Exception {

    }
}
