package org.radrso.workflow.wfservice.repositoy;

import org.radrso.workflow.entity.model.StepProgress;

import java.util.List;

/**
 * Created by Rao-Mengnan
 * on 2017/10/25.
 */
public interface StepProgressRepository extends BaseWFRepository<StepProgress, String> {
    List<StepProgress> findByInstanceId(String instanceId);
}
