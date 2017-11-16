package org.radrso.plugins;

import java.util.regex.Pattern;

/**
 * Created by rao-mengnan on 2017/3/17.
 */
public class StringUtils {
    private static final Pattern INTEGER_PATTERN;

    static {
        INTEGER_PATTERN = Pattern.compile("^[0-9]*");
    }

    public static boolean isInteger(String str) {
        return (str != null && str.length() > 0) && INTEGER_PATTERN.matcher(str).matches();
    }
}
