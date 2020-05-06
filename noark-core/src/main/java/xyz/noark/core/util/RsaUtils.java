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

import xyz.noark.core.lang.ImmutablePair;
import xyz.noark.core.lang.Pair;

import javax.crypto.Cipher;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * RSA加密解密工具类.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.2.1
 */
public class RsaUtils {

    public static void main(String[] args) throws Exception {
        String password = ArrayUtils.isEmpty(args) ? "ps!see(3#K)shit!say(man)" : args[0];
        Pair<String, String> keyPair = genKeyPair(512);
        System.out.println("privateKey:" + keyPair.getLeft());
        System.out.println("publicKey:" + keyPair.getRight());

        String p = encrypt(keyPair.getLeft(), password);
        System.out.println("password:" + p);
        System.out.println(decrypt(keyPair.getRight(), p));
    }

    /**
     * RSA公钥解密.
     *
     * @param publickeyText 公钥
     * @param ciphertext    密文
     * @return 解密后的明文
     */
    public static String decrypt(String publickeyText, String ciphertext) {
        PublicKey publicKey = getPublicKey(publickeyText);
        return decrypt(publicKey, ciphertext);
    }

    public static PublicKey getPublicKey(String publicKeyText) {
        try {
            X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(Base64Utils.decode(publicKeyText));
            return KeyFactory.getInstance("RSA", "SunRsaSign").generatePublic(x509KeySpec);
        } catch (Exception e) {
            throw new IllegalArgumentException("Illegal public key:" + publicKeyText, e);
        }
    }

    public static String decrypt(PublicKey publicKey, String ciphertext) {
        if (StringUtils.isEmpty(ciphertext)) {
            return ciphertext;
        }

        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, publicKey);
            return new String(cipher.doFinal(Base64Utils.decode(ciphertext)));
        } catch (Exception e) {
            throw new IllegalArgumentException("Illegal ciphertext:" + ciphertext, e);
        }
    }

    public static String encrypt(String key, String plainText) throws Exception {
        return encrypt(Base64Utils.decode(key), plainText);
    }

    public static String encrypt(byte[] keyBytes, String plainText) throws Exception {
        PrivateKey privateKey = KeyFactory.getInstance("RSA", "SunRsaSign").generatePrivate(new PKCS8EncodedKeySpec(keyBytes));
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);
        return Base64Utils.encodeToString(cipher.doFinal(plainText.getBytes("UTF-8")));
    }

    /**
     * 生成密钥与公钥.
     *
     * @param keysize 密钥大小。这是一个算法特定的度量，如模长，指定的位数。
     * @return 左边为密钥，右边为公钥
     * @throws NoSuchAlgorithmException if a KeyPairGeneratorSpiimplementation
     *                                  for the specified algorithm is notavailable from the
     *                                  specified provider.
     * @throws NoSuchProviderException  if the specified provider is
     *                                  notregistered in the security provider list.
     */
    public static Pair<String, String> genKeyPair(int keysize) throws NoSuchAlgorithmException, NoSuchProviderException {
        KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA", "SunRsaSign");
        gen.initialize(keysize, new SecureRandom());
        KeyPair pair = gen.generateKeyPair();
        byte[] privateKey = pair.getPrivate().getEncoded();
        byte[] publicKey = pair.getPublic().getEncoded();
        return ImmutablePair.of(Base64Utils.encodeToString(privateKey), Base64Utils.encodeToString(publicKey));
    }
}
