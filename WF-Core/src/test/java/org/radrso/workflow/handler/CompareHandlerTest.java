package org.radrso.workflow.handler;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Rao-Mengnan
 * on 2017/10/25.
 */
public class CompareHandlerTest {
    @Test
    public void compare() throws Exception {
        Boolean a = true;
        Boolean b = false;
        Assert.assertEquals(false, new CompareHandler("=").compare(a, b));
        Assert.assertEquals(true, new CompareHandler(">").compare(a, b));

        boolean c = true;
        boolean d = true;
        Assert.assertEquals(true, new CompareHandler("&&").compare(c, d));
        Assert.assertEquals(true, new CompareHandler("||").compare(c, d));

        Assert.assertEquals(true, new CompareHandler(">=").compare(1, 1));
        Assert.assertEquals(true, new CompareHandler(">").compare(2, 1));
        Assert.assertEquals(true, new CompareHandler("<=").compare(0, 1));
        Assert.assertEquals(true, new CompareHandler("!=").compare(0, 1));

    }

}