package org.radrso.plugins;

import java.io.*;

/**
 * Created by Raomengnan on 2016/9/4.
 */
public class FileUtils {
    private FileUtils() {
    }

    public static boolean deleteFile(String path) {
        File file = new File(path);
        if (file.exists())
            return file.delete();
        return false;
    }

    public static boolean writeFile(String path, String name, byte[] bytes) throws IOException {
        FileOutputStream outputStream = null;
        try {
            File filePath = new File(path);
            if (!filePath.exists())
                filePath.mkdirs();

            File file = new File(filePath, name);
            outputStream = new FileOutputStream(file);
            outputStream.write(bytes);
        } finally {
            if (outputStream != null)
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }

        return true;

    }

    public static byte[] getByte(File file) throws IOException {
        if (file == null || !file.exists())
            return null;
        FileInputStream is = null;
        ByteArrayOutputStream bos = null;
        byte[] buffer = null;
        try {
            is = new FileInputStream(file);
            bos = new ByteArrayOutputStream(1024);
            byte[] buf = new byte[1024];
            int len = 0;
            while ((len = is.read(buf)) != -1)
                bos.write(buf, 0, len);
            buffer = bos.toByteArray();
        } finally {
            try {
                is.close();
                bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return buffer;
    }

    public byte[] getByte(String filePath) throws IOException {
        return getByte(new File(filePath));
    }

    /**
     * 创建一个文件file的上级目录
     *
     * @param file
     */
    public static void mkParentDir(File file) {
        File parentFile = file.getAbsoluteFile().getParentFile();
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }
    }


    public static String getUserHome() {
        String path = System.getProperties().getProperty("user.home") + File.separator;
        return path;
    }

    public static String getProjectHome() {
        return System.getProperty("user.dir") + File.separator;
    }

}
