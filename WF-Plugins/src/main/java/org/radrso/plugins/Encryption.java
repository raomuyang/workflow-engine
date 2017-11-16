package org.radrso.plugins;


import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import java.security.SecureRandom;

/**
 * Created by rao-mengnan on 2017/6/20.
 * DES加密工具
 */
public class Encryption {

    /**
     * @param message 要加密的字符串
     * @param key 秘钥
     * @return 加密字符串
     * @throws Exception
     */
    public static String encrypt(String message, String key) {
        byte[] result;
        try {
            result = encrypt(message.getBytes("UTF-8"), key);
        } catch (Exception e) {
            throw new IllegalStateException();
        }
        return byteArrayToHexStr(result);
    }

    /**
     * 使用DES加密
     *
     * @param message 需要加密数据的byte数组
     * @param key 秘钥
     * @return 加密后的byte数组
     * @throws Exception
     */

    public static byte[] encrypt(byte[] message, String key) throws Exception {

        Cipher cipher = initCipher(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(message);
    }

    /**
     * @param key 秘钥
     * @param hexString 16进制字符串
     * @return 返回解密后的字符串
     * @throws Exception
     */
    public static String decrypt(String hexString, String key) throws Exception {
        byte[] bytes = hexStrToByteArray(hexString);
        return new String(decrypt(bytes, key));
    }

    /**
     * 使用DES解密
     *
     * @param key 秘钥
     * @param message 需要解密数据的byte数组
     * @return 解密后的byte数组
     * @throws Exception
     */
    public static byte[] decrypt(byte[] message, String key) throws Exception {

        Cipher cipher = initCipher(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(message);
    }

    /**
     *
     * @param MODE 加密/解密模式
     * @param key 秘钥
     */
    private static Cipher initCipher(int MODE, String key) throws Exception {
        SecureRandom secureRandom = new SecureRandom();
        DESKeySpec desKeySpec = new DESKeySpec(key.getBytes());
        SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey secretKey = secretKeyFactory.generateSecret(desKeySpec);

        Cipher cipher = Cipher.getInstance("DES");
        cipher.init(MODE, secretKey, secureRandom);
        return cipher;
    }

    private static final char[] HEX = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    public static String byteArrayToHexStr(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            int h = (b >> 4) & 0xf;
            int l = b & 0xf;
            sb.append(HEX[h]).append(HEX[l]);
        }
        return sb.toString();
    }

    public static byte[] hexStrToByteArray(String hexStr) throws Exception {
        if (hexStr.length() % 2 != 0) {
            throw new Exception("Input hex string length must be multiple of 2");
        }
        byte[] bytes = new byte[hexStr.length() / 2];
        for (int i = 0; i < hexStr.length(); i += 2) {
            int h = hexValueMapper(hexStr.charAt(i));
            int l = hexValueMapper(hexStr.charAt(i + 1));

            byte origin = new Integer((h << 4) + l).byteValue();
            bytes[i / 2] = origin;
        }
        return bytes;
    }

    private static int hexValueMapper(char ch) throws Exception {
        if (ch >= '0' && ch <= '9') {
            return Integer.parseInt(ch + "");
        }

        switch (ch) {
            case 'A':
            case 'a':
                return 10;
            case 'B':
            case 'b':
                return 11;
            case 'C':
            case 'c':
                return 12;
            case 'D':
            case 'd':
                return 13;
            case 'E':
            case 'e':
                return 14;
            case 'F':
            case 'f':
                return 15;
            default:
                throw new Exception("Input must be hex string");

        }
    }

}

