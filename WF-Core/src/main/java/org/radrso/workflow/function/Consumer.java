package org.radrso.workflow.function;

/**
 * Created by Rao-Mengnan
 * on 2017/10/23.
 */
public interface Consumer<T> {
    /**
     * Consume the given value.
     * @param t value
     * @throws Exception on error
     */
    void accept(T t) throws Exception;
}
