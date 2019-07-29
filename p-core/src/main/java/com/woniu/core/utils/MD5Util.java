package com.woniu.core.utils;

import android.util.Log;

import com.blankj.utilcode.util.EncryptUtils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

public class MD5Util {
    public static  String  nonceStr = UUID.randomUUID().toString().replace("-", ""); // 唯一随机数\

    public static String stringToMD5(String string) {
        byte[] hash;

        try {
            hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            return null;
        } catch (UnsupportedEncodingException e) {
            return null;
        }

        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10)
                hex.append("0");
            hex.append(Integer.toHexString(b & 0xFF));
        }

        return hex.toString();
    }

    /**
     * String replace = UUID.randomUUID().toString().replace("-", "");
     * long timestamp = System.currentTimeMillis();//毫秒时间戳;
     * String s = EncryptUtils.encryptMD5ToString(replace + timestamp);
     * Log.e("MD555555",s);
     */

    //UUID + 时间戳 生成MD5
    public static String UUID2MD5() {
        String replace = UUID.randomUUID().toString().replace("-", "");
        long timestamp = System.currentTimeMillis();//毫秒时间戳;
        String s = EncryptUtils.encryptMD5ToString(replace + timestamp);
        Log.e("MD555555", s);
        return s;
    }
}
