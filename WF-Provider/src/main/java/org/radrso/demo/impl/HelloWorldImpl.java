package org.radrso.demo.impl;

import org.radrso.demo.HelloWorld;
import org.radrso.demo.impl.test.Test1Impl;
import org.radrso.demo.impl.test.Test2Impl;
import org.radrso.demo.test.Test1;
import org.radrso.demo.test.Test2;
import org.springframework.stereotype.Service;

/**
 * Created by raomengnan on 16-12-2.
 */
@Service
public class HelloWorldImpl implements HelloWorld {
    public Test1 test1 = new Test1Impl();
    public Test2 test2 = new Test2Impl();

    @Override
    public String say(String str) {
        return "Hello World:" + str;
    }

    @Override
    public String useTest1() {
        return test1.test1();
    }

    @Override
    public String useTest2() {
        return test2.test2();
    }
}
