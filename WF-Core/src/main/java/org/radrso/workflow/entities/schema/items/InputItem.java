package org.radrso.workflow.entities.schema.items;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 * Created by raomengnan on 16-12-8.
 * name     参数名（可选）
 * value    参数
 * type     参数类型
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class InputItem implements Serializable{
    private String name = "default";
    private String value;
    private String type = "String";
}
