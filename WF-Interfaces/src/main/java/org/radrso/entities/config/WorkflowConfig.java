package org.radrso.entities.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.radrso.entities.config.items.Step;

import java.util.Date;
import java.util.List;

/**
 * Created by raomengnan on 16-12-8.
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class WorkflowConfig {
    private String application;
    private String id;
    private String header;
    private Date startTime;
    private Date stopTime;
    private List<Step> steps;
}
