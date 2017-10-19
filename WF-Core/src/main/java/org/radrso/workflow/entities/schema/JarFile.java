package org.radrso.workflow.entities.schema;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Created by rao-mengnan on 2017/4/16.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Document(collection = "jars")
public class JarFile {
    @Id
    private String id;
    private String application;
    private String name;
    private byte[] file;
}
