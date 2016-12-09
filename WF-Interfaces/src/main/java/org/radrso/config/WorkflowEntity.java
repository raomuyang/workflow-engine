package org.radrso.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.radrso.config.items.DoOnNext;
import org.radrso.config.items.InputItem;

/**
 * Created by raomengnan on 16-12-8.
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class WorkflowEntity {
    private String application;
    private String workflow;
    private InputItem[] input;
    private String header;
    private DoOnNext[] run;
}
