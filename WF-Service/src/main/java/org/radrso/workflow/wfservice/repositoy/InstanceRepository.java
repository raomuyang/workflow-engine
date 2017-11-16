package org.radrso.workflow.wfservice.repositoy;

import org.radrso.workflow.entity.model.Instance;

import java.util.List;

/**
 * Created by Rao-Mengnan
 * on 2017/10/25.
 */
public interface InstanceRepository extends BaseWFRepository<Instance, String> {
    List<Instance> findByWorkflowId(String workflowId);
}
