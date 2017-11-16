package org.radrso.workflow.function;

/**
 * Created by Rao-Mengnan
 * on 2017/10/23.
 */
public interface ConversionParam1 <T, R> {
    R mapTo(T t) throws Exception;
}
