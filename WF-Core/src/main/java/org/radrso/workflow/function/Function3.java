package org.radrso.workflow.function;

/**
 * Created by Rao-Mengnan
 * on 2017/10/25.
 */
public interface Function3<T1, T2, T3, R> {
    R apply(T1 t1, T2 t2, T3 t3) throws Exception;
}
