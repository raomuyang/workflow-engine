package org.radrso.entities.config.items;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 * Created by raomengnan on 16-12-8.
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class InputItem implements Serializable{
    private String name;
    private String value;
    private String type;
}
