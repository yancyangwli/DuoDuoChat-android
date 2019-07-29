package com.woniu.core.utils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.logging.Logger;

/**
 * Created by leo on 2017/10/18.
 */
public class AESUtils {


        private static final String KEY_ALGORITHM = "AES";
        private static final String DEFAULT_CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding";//默认的加密算法


        /**
         * AES 加密操作
         *
         * @param content 待加密内容
         * @param encryptKey 加密密码
         * @return 返回Base64转码后的加密数据
         */
        public static String encrypt(String content, String encryptKey) {
            try {
                Cipher cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);// 创建密码器

                byte[] byteContent = content.getBytes("utf-8");

                cipher.init(Cipher.ENCRYPT_MODE, getSecretKey(encryptKey));// 初始化为加密模式的密码器

                byte[] result = cipher.doFinal(byteContent);// 加密

                return  ByteUtil.bytesToHexString(result); 
            } catch (Exception ex) {
            }

            return null;
        }


        /**
         * AES 解密操作
         *
         * @param content
         * @param encryptKey
         * @return
         */
        public static String decrypt(String content, String encryptKey) {

            try {
                //实例化
                Cipher cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);

                //使用密钥初始化，设置为解密模式
                cipher.init(Cipher.DECRYPT_MODE, getSecretKey(encryptKey));

                //执行操作
                byte[] result = cipher.doFinal(ByteUtil.hexStringToByte(content));

                return new String(result, "utf-8");
            } catch (Exception ex) {

            }

            return null;
        }


        /**
         * 生成加密秘钥
         *
         * @return
         */
        private static SecretKeySpec getSecretKey(final String encryptKey) {
            //返回生成指定算法密钥生成器的 KeyGenerator 对象
            KeyGenerator kg = null;

            try {
                kg = KeyGenerator.getInstance(KEY_ALGORITHM);
                SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
                secureRandom.setSeed(encryptKey.getBytes());
                //AES 要求密钥长度为 128
                kg.init(128, secureRandom);

                //生成一个密钥
                SecretKey secretKey = kg.generateKey();
                 return new SecretKeySpec(secretKey.getEncoded(), KEY_ALGORITHM);// 转换为AES专用密钥
            } catch (NoSuchAlgorithmException ex) {
            }

            return null;
        }

   public static void main(String[] args) {
	   System.out.println(encrypt("张辉超","123qwe"));
	   System.out.println(decrypt("2400AF9E64BE0FE7684E94D1FAEE3C79", "123qwe"));
}
    }

