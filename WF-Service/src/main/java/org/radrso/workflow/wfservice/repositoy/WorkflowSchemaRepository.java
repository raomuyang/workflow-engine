package org.radrso.workflow.wfservice.repositoy;

import org.radrso.workflow.entities.schema.WorkflowSchema;

/**
 * Created by Rao-Mengnan
 * on 2017/10/25.
 */
public interface WorkflowSchemaRepository extends BaseWFRepository<WorkflowSchema, String> {
    WorkflowSchema findByApplication(String application);
    WorkflowSchema findByOwner(String owner);
}
