package org.radrso.workflow.actions;

import io.reactivex.functions.Action;
import org.radrso.workflow.base.Commander;
import org.radrso.workflow.entities.model.WorkflowInstance;
import org.radrso.workflow.resolvers.WorkflowResolver;

/**
 * Created by rao-mengnan on 2017/5/18.
 */
public class OnExecuteNextAction extends AbstractAction implements Action {
    public WorkflowResolver resolver;

    public OnExecuteNextAction(WorkflowResolver resolver, Commander workflowSynchronize) {
        super(workflowSynchronize);
        this.resolver = resolver;
    }

    @Override
    public void run() throws Exception {
        WorkflowInstance instance = resolver.getWorkflowInstance();
        instance.setStatus(WorkflowInstance.RUNNING);
        commander.updateInstance(instance);

        Actions.startStream(resolver, commander);
    }

    @Override
    public AbstractAction setResolver(WorkflowResolver resolver) {
        this.resolver = resolver;
        return this;
    }
}
