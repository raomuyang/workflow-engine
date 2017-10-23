package org.radrso.workflow.entities;

/**
 * Created by Rao-Mengnan
 * on 2017/10/19.
 */
public enum  StatusEnum {
    CREATED("created"),
    EXPIRED("expired"),
    WAIT("waiting"),
    RUNNING("running"),
    COMPLETED("completed"),
    EXCEPTION("exception"),
    INTERRUPTED("interrupted");

    String value;
    StatusEnum(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
