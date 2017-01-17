package org.radrso.plugins;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Raomengnan on 2016/9/4.
 */
public class FileUtils {

    public static boolean deleteFile(String path){
        File file = new File(path);
        if(file.exists())
            return file.delete();
        return false;
    }

    public static boolean writeFile(String path, String name, byte[] bytes) throws IOException {
        FileOutputStream outputStream = null;
        try {
            File filePath = new File(path);
            if(!filePath.exists())
                filePath.mkdirs();

            File file = new File(filePath, name);
            outputStream = new FileOutputStream(file);
            outputStream.write(bytes);
        }finally {
            if(outputStream != null)
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }

        return true;

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


    public static String getUserHome(){
        String path = System.getProperties().getProperty("user.home") + File.separator;
        return path;
    }

    public static String getProjectHome(){
        return System.getProperty("user.dir") + File.separator;
    }

}
