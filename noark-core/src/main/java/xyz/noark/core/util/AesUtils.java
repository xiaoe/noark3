/*
 * Copyright © 2018 www.noark.xyz All Rights Reserved.
 *
 * 感谢您选择Noark框架，希望我们的努力能为您提供一个简单、易用、稳定的服务器端框架 ！
 * 除非符合Noark许可协议，否则不得使用该文件，您可以下载许可协议文件：
 *
 * 		http://www.noark.xyz/LICENSE
 *
 * 1.未经许可，任何公司及个人不得以任何方式或理由对本框架进行修改、使用和传播;
 * 2.禁止在本项目或任何子项目的基础上发展任何派生版本、修改版本或第三方版本;
 * 3.无论你对源代码做出任何修改和改进，版权都归Noark研发团队所有，我们保留所有权利;
 * 4.凡侵犯Noark版权等知识产权的，必依法追究其法律责任，特此郑重法律声明！
 */
package xyz.noark.core.util;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * AES算法工具类
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.3.3
 */
public class AesUtils {
    /**
     * 算法名称：AES
     */
    private static final String ALGORITHM_AES = "AES";

    /**
     * 使用AES算法加密指定文本.
     * <p>
     * 规则1，文本以UTF-8的方式转化为2进制数组.<br>
     * 规则2，对数组进行AES算法加密.<br>
     * 规则3，加密结果BASE64编码，使结果友好显示<br>
     * 注：如果需要其他规则方法，请使用{@link AesUtils#encrypt(byte[], String)}
     *
     * @param text 待加密的文本
     * @param key  加密密钥
     * @return 加密后的结果
     */
    public static String encrypt(String text, String key) {
        return Base64Utils.encodeToString(encrypt(StringUtils.utf8Bytes(text), key));
    }

    /**
     * 使用AES算法加密指定数据
     *
     * @param data 需要加密的数据
     * @param key  加密密钥
     * @return 加密后的结果
     */
    public static byte[] encrypt(byte[] data, String key) {
        return doAes(data, Cipher.ENCRYPT_MODE, key);
    }

    /**
     * 使用AES算法解密指定文本.
     * <p>
     * 规则1，文本以BASE64的方式解码为2进制数组.<br>
     * 规则2，对数组进行AES算法解密.<br>
     * 规则3，对结果使用UTF-8的方式转化为字符串<br>
     * 注：如果需要其他规则方法，请使用{@link AesUtils#decrypt(byte[], String)}
     *
     * @param text 待解密的文本
     * @param key  解密密钥
     * @return 解密后的结果
     */
    public static String decrypt(String text, String key) {
        return new String(decrypt(Base64Utils.decode(text), key), CharsetUtils.CHARSET_UTF_8);
    }

    /**
     * 使用AES算法解密指定数据.
     *
     * @param data 待解密的数据
     * @param key  解密密钥
     * @return 解密后的结果
     */
    public static byte[] decrypt(byte[] data, String key) {
        return doAes(data, Cipher.DECRYPT_MODE, key);
    }

    /**
     * 开始AES算法逻辑.
     *
     * @param data 待处理数据
     * @param mode 加密{@link Cipher#ENCRYPT_MODE}or解密{@link Cipher#DECRYPT_MODE}
     * @param key  密钥Key
     * @return 加密或解密结果
     */
    private static byte[] doAes(byte[] data, int mode, String key) {
        try {
            // 构造AES算法密钥
            SecretKey secretKey = genSecretKey(key);

            // AES密码算法
            Cipher cipher = Cipher.getInstance(ALGORITHM_AES);
            cipher.init(mode, new SecretKeySpec(secretKey.getEncoded(), ALGORITHM_AES));

            // 开始计算
            return cipher.doFinal(data);
        } catch (Exception e) {
            throw new IllegalArgumentException("Illegal Argument.", e);
        }
    }

    /**
     * 构造AES算法密钥.
     *
     * @param key 密钥Key
     * @return AES算法密钥
     */
    private static SecretKey genSecretKey(final String key) {
        try {
            KeyGenerator kgen = KeyGenerator.getInstance(ALGORITHM_AES);
            kgen.init(128, new SecureRandom(key.getBytes()));
            return kgen.generateKey();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException("illegal algorithm aes", e);
        }
    }
}