package org.radrso.workflow.jobdemo;

/**
 * Created by rao-mengnan on 2017/3/13.
 */
public class SimpleCompute {

    public int sum(Integer... args) {
        int sum = 0;
        if (args.length > 0) {
            for (int i = 0; i < args.length; ++i) {
                sum += args[i];
            }
        }
        return sum;
    }

    public double arerage(Integer ...args){
        double sum = 0;
        if (args.length > 0) {
            for (int i = 0; i < args.length; ++i) {
                sum += args[i];
            }
        }
        return sum / args.length;
    }
}
