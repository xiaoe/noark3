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
package xyz.noark.network;

import java.util.UUID;

import xyz.noark.core.lang.ByteArray;
import xyz.noark.core.network.PacketEncrypt;
import xyz.noark.core.util.CharsetUtils;
import xyz.noark.core.util.StringUtils;

/**
 * Noark提供的一种加密方式.
 * <p>
 * 利用一组数字对协议进行循环异或
 *
 * @since 3.1
 * @author 小流氓(176543888@qq.com)
 */
public class DefaultPacketEncrypt implements PacketEncrypt {
	/** 密钥 */
	private final byte[] secretKey;
	/** 公钥 */
	private final byte[] publicKey;
	/** 是否使用加密方案 */
	private final boolean encrypt;

	public DefaultPacketEncrypt(boolean encrypt, byte[] secretKey) {
		this.encrypt = encrypt;
		this.secretKey = secretKey;
		// 随机生成一个公钥
		this.publicKey = StringUtils.utf8Bytes(encrypt ? UUID.randomUUID().toString() : StringUtils.EMPTY);
	}

	@Override
	public boolean isEncrypt() {
		return encrypt;
	}

	@Override
	public String getPublicKey() {
		return new String(publicKey, CharsetUtils.CHARSET_UTF_8);
	}

	@Override
	public void decode(ByteArray data, int incode) {
		int publicKeyIndex = 0;
		int privateKeyIndex = 0;
		// XOR
		for (int i = 0, len = data.length(); i < len; i++) {
			byte value = data.getByte(i);
			value ^= secretKey[privateKeyIndex++ % secretKey.length];
			value ^= publicKey[publicKeyIndex++ % publicKey.length];
			value ^= incode << 2;
			data.setByte(i, value);
		}
		// 备选方案
		// 首尾交换 [0]=[len-1]
		// 补码取反 ~value
		// 高低互换 (value << 4) | (value >> 4)
	}
}