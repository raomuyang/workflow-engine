package org.radrso.plugins;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Raomengnan on 2016/5/30.
 */
public class MD5 {
    public static String getMD5(String str){
        char[] hexDict = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            byte[] md5 =messageDigest.digest(str.getBytes());
            char[] strMd5 = new char[md5.length * 2];
            int k = 0;
            for (byte b : md5){
                //Integer中也有转换16进制的方法
                //(b&0xff>>4) == (b & 0xf)
                //4bit表示一个16进制数
                strMd5[k++] = hexDict[(b >> 4)& 0xf];// b >> 4 :高四位的为16进制的第一个数
                strMd5[k++] = hexDict[ b & 0xf]; //低四位的为16进制第二个数字
            }
            return new String(strMd5);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }

    }
}
