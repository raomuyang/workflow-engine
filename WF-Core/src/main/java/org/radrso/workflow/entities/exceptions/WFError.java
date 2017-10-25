package org.radrso.workflow.entities.exceptions;

/**
 * Created by Rao-Mengnan
 * on 2017/10/25.
 */
public interface WFError {
   int getCode();
   String getDetailMessage();
}
