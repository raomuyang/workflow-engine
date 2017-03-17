package org.radrso.workflow.jobdemo;

/**
 * Created by rao-mengnan on 2017/3/17.
 */
public class Hello {
    public String hello(String name){
        return "hello," + name;
    }

    public String wakeup(String name){
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return String.format("Already wake %s up", name);
    }

    public String finish(String name){
        return name + " finished the day";
    }
}
