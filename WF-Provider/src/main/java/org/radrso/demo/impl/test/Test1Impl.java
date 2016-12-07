package org.radrso.demo.impl.test;

import org.radrso.demo.test.Test1;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

/**
 * Created by raomengnan on 16-12-2.
 */
@Service
public class Test1Impl implements Test1 {
    @Override
    public String test1() {
        return "test1";
    }
}
