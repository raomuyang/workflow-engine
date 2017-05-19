package org.radrso.plugins;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by raomengnan on 16-12-10.
 */
public class BinaryUtil {

    private BinaryUtil() {
    }

    private static final char[] hexDict = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    public static String string2MD5(String str) {
        return calculateMD5(str.getBytes());
    }

    public static String calculateMD5(byte[] bytes) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            byte[] md5 = messageDigest.digest(bytes);
            char[] strMd5 = new char[md5.length * 2];
            int k = 0;
            for (byte b : md5) {
                //Integer中也有转换16进制的方法
                //(b & 0xff >> 4) == (b & (0xff >> 4)) == (b & 0xf)
                //4bit表示一个16进制数
                strMd5[k++] = hexDict[(b >> 4) & 0xf];// b >> 4 :高四位的为16进制的第一个数
                strMd5[k++] = hexDict[b & 0xf]; //低四位的为16进制第二个数字
            }
            return new String(strMd5);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }

    }

    public static String readFromStream(InputStream stream) throws IOException {

        BufferedReader reader = null;
        StringBuilder sb = new StringBuilder();
        try {
            reader = new BufferedReader(new InputStreamReader(stream));

            String line = null;
            while ((line = reader.readLine()) != null)
                sb.append(line);
        } finally {
            reader.close();
            stream.close();

        }
        return sb.toString();
    }

    /**
     * 从流中读取一行数据
     *
     * @param inputStream
     * @return
     * @throws IOException
     */
    public static String readLine(PushbackInputStream inputStream) throws IOException {
        char buf[] = new char[128];
        int rom = buf.length;
        int offect = 0;
        int ch;

        loop:
        while (true) {
            switch (ch = inputStream.read()) {
                case -1:
                    ;
                case '\n':
                    break loop;
                case '\r':
                    int ch2 = inputStream.read();
                    if (ch2 != '\n' || ch2 != -1) {
                        inputStream.unread(ch2);
                    }
                    break loop;
                default:
                    if (--rom < 0) {//字符串数组容量不够
                        char[] bufBak = buf;
                        buf = new char[offect + 128];
                        rom = buf.length - offect - 1;
                        System.arraycopy(bufBak, 0, buf, 0, offect);
                    }
                    buf[offect++] = (char) ch;
                    break;
            }
        }

        if (ch == -1 && offect == 0) return null;
        return String.copyValueOf(buf, 0, offect);

    }

    /**
     * 读取输入流数据.不适合读取数据量较大的数据流
     *
     * @param inputStream
     * @return
     * @throws IOException
     */
    public static byte[] readStream(InputStream inputStream) throws IOException {
        byte[] buf = new byte[1024];
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        int len = -1;
        while ((len = inputStream.read(buf)) != -1)
            outputStream.write(buf, 0, len);
        outputStream.close();
        return outputStream.toByteArray();
    }

}
