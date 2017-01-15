package org.radrso.workflow.provider.requests.entity;

import java.io.Serializable;

/**
 * Created by raomengnan on 16-12-9.
 */
public enum Method implements Serializable{
    GET("Get"),
    POST("Post"),
    PUT("Put"),
    DELETE("Delete");

    String methodName;
    Method(String name) {
        this.methodName = name;
    }

    public String value(){return this.methodName;}
}
