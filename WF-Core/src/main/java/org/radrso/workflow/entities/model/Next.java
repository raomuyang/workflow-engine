package org.radrso.workflow.entities.model;

import lombok.Data;
import org.radrso.workflow.entities.schema.items.Transfer;

/**
 * Created by Rao-Mengnan
 * on 2017/10/19.
 */
@Data
public class Next {
    String precursor;
    Transfer transfer;
    StepProcess process;
}
