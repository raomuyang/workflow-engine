package org.radrso.workflow.wfservice.repositoy;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Rao-Mengnan
 * on 2017/10/25.
 * In order to compatible with {@link org.springframework.data.mongodb}
 */
public interface BaseWFRepository<T, ID extends Serializable> {
    T findOne(ID var1);

    <S extends T> S save(S var1);

    <S extends T> List<S> save(Iterable<S> entity);

    List<T> findAll();

    <S extends T> S insert(S entity);

    <S extends T> List<S> insert(Iterable<S> entity);

    void delete(ID id);

}