package org.radrso.workflow.handler;


import org.radrso.workflow.entities.exceptions.ConfigReadException;

/**
 * Created by Rao-Mengnan
 * on 2017/10/23.
 */
public class CompareHandler {

    String condition;
    public CompareHandler(String condition) {
        this.condition = condition;
    }

    public boolean compare(Object src, Object target) throws ConfigReadException {
        Comparable a = (Comparable) src;
        Comparable b = (Comparable) target;
        switch (condition) {
            case ">":
                return a.compareTo(b) > 0;
            case "=":
            case "==":
                return (a.compareTo(b) == 0);
            case "<":
                return (a.compareTo(b) < 0);
            case ">=":
                return (a.compareTo(b) >= 0);
            case "<=":
                return (a.compareTo(b) <= 0);
            case "&&":
                return ((Boolean) src && (Boolean) target);
            case "||":
                return ((Boolean) src || (Boolean) target);
            default:
                throw new ConfigReadException("Unknown expression: " + condition);
        }
    }
}
