package org.radrso.plugins.requests.entity;

/**
 * Created by raomengnan on 16-12-9.
 */
public enum Method {
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
