package org.radrso.entities.config.items;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Created by raomengnan on 17-1-8.
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Task {
    private String id;
    private String name;
    private String submitPath;
    private String callback;
}
