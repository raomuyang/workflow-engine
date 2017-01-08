package org.radrso.entities.wf;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * Created by raomengnan on 17-1-4.
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Workflow {
    private String application;
    private String id;
    private Date startTime;
    private Date stopTime;
    private boolean isStart;
}
