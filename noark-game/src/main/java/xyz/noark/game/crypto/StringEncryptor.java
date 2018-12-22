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
package xyz.noark.game.crypto;

import java.util.HashMap;

import xyz.noark.core.util.RsaUtils;
import xyz.noark.core.util.StringUtils;
import xyz.noark.game.NoarkConstant;

/**
 * 字符串加密器.
 *
 * @since 3.2.1
 * @author 小流氓(176543888@qq.com)
 */
public class StringEncryptor {
	/** RSA加密方案默认公钥 */
	private static final String DEFAULT_RSA_PUBLICKEY = "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAMon8WPYh6POFo4J6p2uEwP0Pau5FNtDUmybagCwbtCFfAZ9pnB1HN0Cnvzn8eAO+BGeqjnwYnjmo0CWp328nHECAwEAAQ==";
	/** RSA加密方案默认私钥 */
	private static final String DEFAULT_RSA_PRIVATEKEY = "MIIBVAIBADANBgkqhkiG9w0BAQEFAASCAT4wggE6AgEAAkEAyifxY9iHo84Wjgnqna4TA/Q9q7kU20NSbJtqALBu0IV8Bn2mcHUc3QKe/Ofx4A74EZ6qOfBieOajQJanfbyccQIDAQABAkEAvFsBIB3VK/vOqiN1NdvGne2JNKJrW2zbtJQN7Xx2k9PS/ojbbvTPMp2CQ5PNVfuTv5gwGrix1ecODbxZufH4LQIhAPM4a4px6YBM3U0BCcu9ohUcTDNPtvcU1y5HviYAknrzAiEA1McpjJTvDfC5Er3D/jUAj3uc8qMRU0g4G0XEGow2XAsCIFUgmDM6r3libcp35I/U7Zfp8Zm7+tP8DVx7a8gtRxeVAiBNvEFiudqoRiTnQusCyUxeHzQUtRyUR5Mv64ochNMrRwIgU3okWBEZZFvb/rAy9Xs6HneN+Ilh0L/r614SOsC9A2Q=";
	/** RSA加密方案前缀 */
	private static final String RSA_CRYPTO_PREFIX = "rsa:";

	private final String rsaPublickeyText;

	public StringEncryptor(HashMap<String, String> properties) {
		this.rsaPublickeyText = properties.getOrDefault(NoarkConstant.CRYPTO_RSA_PUBLICKEY, DEFAULT_RSA_PUBLICKEY);
	}

	public String decrypt(String ciphertext) {
		// 如果是个空，那就还返回原来的参数...
		if (StringUtils.isEmpty(ciphertext)) {
			return ciphertext;
		}

		// RSA加密
		if (ciphertext.startsWith(RSA_CRYPTO_PREFIX)) {
			return RsaUtils.decrypt(rsaPublickeyText, ciphertext.substring(RSA_CRYPTO_PREFIX.length()));
		}

		// 非加密方案直接返回
		return ciphertext;
	}

	public static void main(String[] args) throws Exception {
		// 加密明文密码（私钥加密，公钥解密）...
		System.out.println("rsa publickey=" + DEFAULT_RSA_PUBLICKEY);
		System.out.println("rsa privatekey=" + DEFAULT_RSA_PRIVATEKEY);
		System.out.println();
		for (String password : args) {
			System.out.println(password + "=" + RsaUtils.encrypt(DEFAULT_RSA_PRIVATEKEY, password));
		}
		System.out.println();
		System.out.println("success.");
	}
}