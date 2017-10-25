package org.radrso.workflow.handler;


/**
 * Created by Rao-Mengnan
 * on 2017/10/23.
 */
public class CompareHandler {

    String condition;
    public CompareHandler(String condition) {
        this.condition = condition;
    }

    public boolean compare(Object src, Object target) throws RuntimeException {
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
                throw new RuntimeException("Unknown condition: " + condition);
        }
    }
}
