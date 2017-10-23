package org.radrso.workflow.function;

/**
 * Created by Rao-Mengnan
 * on 2017/10/23.
 */
public interface Condition<T1, T2, R> {
    R check(T1 t1, T2 t2) throws Exception;
}
