package org.radrso.demo.impl.test;

import org.radrso.demo.test.Test2;
import org.springframework.stereotype.Service;

/**
 * Created by raomengnan on 16-12-2.
 */
@Service
public class Test2Impl implements Test2 {
    @Override
    public String test2() {
        return "test2";
    }
}
