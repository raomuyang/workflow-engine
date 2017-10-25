package org.radrso.workflow.internal.model;

import lombok.Data;
import org.radrso.workflow.entities.model.StepProgress;
import org.radrso.workflow.entities.schema.items.Step;
import org.radrso.workflow.entities.schema.items.Transfer;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Rao-Mengnan
 * on 2017/10/19.
 */
@Data
public class Next implements Serializable {
    String precursor;
    Step stepInfo;
    private Date deadline;
    StepProgress progress;

    public List<Map<String, Object>> getParams() {
        return progress.getParams();
    }
}
