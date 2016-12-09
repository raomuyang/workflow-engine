package org.radrso.plugins;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by raomengnan on 16-12-10.
 */
public class StreamUtils {
    public static String readFromStream(InputStream stream) throws IOException {

        BufferedReader reader = null;
        StringBuilder sb = new StringBuilder();
        try {
            reader = new BufferedReader(new InputStreamReader(stream));

            String line = null;
            while ( ( line = reader.readLine() ) != null)
                sb.append(line);
        }finally {
            reader.close();
            stream.close();

        }
        return sb.toString();
    }
}
