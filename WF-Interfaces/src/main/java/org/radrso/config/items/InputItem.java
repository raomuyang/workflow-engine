package org.radrso.config.items;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Created by raomengnan on 16-12-8.
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class InputItem {
    private String name;
    private String value;
    private String type;
}
