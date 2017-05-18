package org.radrso.plugins;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Raomengnan on 2016/9/4.
 */
public class FileUtils {
    private static final char[] DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    public static boolean deleteDir(File dir) {
        if (!dir.exists()) {
            return true;
        }

        try {
            if (dir.isDirectory() && !isSymlink(dir)) {
                String[] children = dir.list();
                for (String s : children) {
                    boolean flag = deleteDir(new File(dir, s));
                    if (!flag) {
                        return false;
                    }
                }
            }
        } catch (IOException e) {
            return false;
        }

        return dir.delete();
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

    public static void renameTo(String srcFilePath, String targetPath) throws IOException {
        Path source = Paths.get(srcFilePath);
        Path target = Paths.get(targetPath);
        Files.move(source, target);
    }

    public static byte[] getMD5Digits(File file) throws IOException {
        FileInputStream inputStream = new FileInputStream(file);
        FileChannel channel = inputStream.getChannel();
        try {
            MessageDigest messagedigest = MessageDigest.getInstance("MD5");
            long position = 0;
            long remaining = file.length();
            MappedByteBuffer byteBuffer = null;
            while (remaining > 0) {
                long size = Integer.MAX_VALUE / 2;
                if (size > remaining) {
                    size = remaining;
                }
                byteBuffer = channel.map(FileChannel.MapMode.READ_ONLY, position, size);
                messagedigest.update(byteBuffer);
                position += size;
                remaining -= size;
            }
            unMapBuffer(byteBuffer, channel.getClass());
            return messagedigest.digest();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        } finally {
            channel.close();
            inputStream.close();
        }
    }

    public static String getFileMD5(File file) throws IOException {
        byte[] bytes = getMD5Digits(file);
        if (bytes == null) {
            return null;
        }
        StringBuilder md5 = new StringBuilder(bytes.length * 2);

        for (int i = 0; i < 16; i++) {
            int high = bytes[i] >> 4 & 0xf;
            int low = bytes[i] & 0xf;
            md5.append(DIGITS[high]);
            md5.append(DIGITS[low]);
        }

        return md5.toString();
    }

    public static <T> boolean writeObject(T object, String filePath) throws IOException {
        File cache = new File(filePath);
        if (cache.exists()) {
            cache.delete();
        }

        ObjectOutputStream objectOutputStream = null;
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(cache);
            objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(object);
            return true;
        } finally {
            try {
                if (objectOutputStream != null) {
                    objectOutputStream.close();
                    fileOutputStream.close();
                }
            } catch (IOException e) {
            }
        }
    }

    public static <T> T readLocalObject(String localPath) throws IOException {
        File file = new File(localPath);
        ObjectInputStream objectInputStream = null;
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(file);
            objectInputStream = new ObjectInputStream(fileInputStream);
            return (T) objectInputStream.readObject();
        } catch (ClassNotFoundException e) {
            return null;
        } finally {
            try {
                if (objectInputStream != null) {
                    objectInputStream.close();
                    fileInputStream.close();
                }
            } catch (IOException e) {
            }
        }
    }

    /**
     * JDK不提供MappedByteBuffer的释放，但是MappedByteBuffer在Full GC时才被回收，通过手动释放的方式让其回收
     *
     * @param buffer
     */
    public static void unMapBuffer(MappedByteBuffer buffer, Class channelClass) throws IOException {
        if (buffer == null) {
            return;
        }

        Throwable throwable = null;
        try {
            Method unmap = channelClass.getDeclaredMethod("unmap", MappedByteBuffer.class);
            unmap.setAccessible(true);
            unmap.invoke(channelClass, buffer);
        } catch (NoSuchMethodException e) {
            throwable = e;
        } catch (IllegalAccessException e) {
            throwable = e;
        } catch (InvocationTargetException e) {
            throwable = e;
        }

        if (throwable != null) {
            throw new IOException("MappedByte buffer unmap error", throwable);
        }
    }

    /**
     * 递归获取该目录下的所有子文件路径（相对于根目录的子路径）
     *
     * @param directory 目录绝对路径，如 ~/path/files/
     * @return 基于根目录的子文件目录:
     * <p>如 ~/path/files/下有 f1.t, f2.t, subPath/f3.t等文件，则返回[f1.t, f2.t, subPath/f3.t] </p>
     * <p>给定目录不存在或文件时，返回null</p>
     */
    public static List<String> getSubFiles(String directory) {
        directory = (directory + "/").replace("~/", getUserHome() + File.separator);
        while (directory.contains("//")) {
            directory = directory.replaceAll("//", "/");
        }

        File dir = new File(directory);
        if (!dir.exists() || dir.isFile()) {
            return null;
        }

        List<String> subFiles = new ArrayList<>();
        addToSubFileList(directory, null, subFiles);
        return subFiles;
    }

    /**
     * @param root     根目录绝对路径
     * @param sub      子目录绝对路径
     * @param subFiles 于根目录的子文件路径列表
     */
    private static void addToSubFileList(String root, String sub, List<String> subFiles) {
        if (sub == null) {
            sub = root;
        }
        File subFilePath = new File(sub);
        if (!subFilePath.exists()) {
            return;
        }

        if (subFilePath.isFile()) {
            String fileName = subFilePath.getAbsolutePath().split(root)[1];
            subFiles.add(fileName);
            return;
        }

        String[] fileList = subFilePath.list();
        if (fileList == null) {
            return;
        }

        for (String subPath : fileList) {
            addToSubFileList(root, sub + File.separator + subPath, subFiles);
        }
    }

    public static boolean isSymlink(File file) throws IOException {
        if (file == null) {
            throw new IOException("File object is null");
        }
        File canonicalFile;
        if (file.getParent() == null) {
            canonicalFile = file;
        } else {
            File canonDir = file.getParentFile().getCanonicalFile();
            canonicalFile = new File(canonDir, file.getName());
        }
        return !canonicalFile.getCanonicalFile().equals(canonicalFile.getAbsoluteFile());
    }

    private FileUtils() {
    }

}
