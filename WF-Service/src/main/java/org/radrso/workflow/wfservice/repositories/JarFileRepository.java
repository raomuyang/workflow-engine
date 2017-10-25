package org.radrso.workflow.wfservice.repositories;

import org.radrso.workflow.entities.model.JarFile;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by rao-mengnan on 2017/4/16.
 */
@Repository
public interface JarFileRepository extends MongoRepository<JarFile, String>{
    List<JarFile> findByApplication(String application);
    JarFile findByApplicationAndName(String application, String name);
}
