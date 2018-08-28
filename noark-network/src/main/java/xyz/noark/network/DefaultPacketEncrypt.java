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

import java.nio.charset.Charset;
import java.util.UUID;

import xyz.noark.core.lang.ByteArray;
import xyz.noark.core.network.PacketEncrypt;

/**
 * Noark提供的一种加密方式.
 * <p>
 * 利用一组数字对协议进行循环异或
 *
 * @since 3.1
 * @author 小流氓(176543888@qq.com)
 */
public class DefaultPacketEncrypt implements PacketEncrypt {
	private static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");
	/** 无边落木萧萧下，不尽长江滚滚来 */
	private static final byte[] PRIVATE_KEY = "do{ManyLeavesFly();YangtzeRiverFlows();}while(1==1);".getBytes(DEFAULT_CHARSET);
	private final String publicKeyCache;
	private final byte[] public_key;

	private final boolean encrypt;

	public DefaultPacketEncrypt(boolean encrypt) {
		this.encrypt = encrypt;
		this.publicKeyCache = UUID.randomUUID().toString();
		this.public_key = publicKeyCache.getBytes(DEFAULT_CHARSET);
	}

	@Override
	public boolean isEncrypt() {
		return encrypt;
	}

	@Override
	public String getPublicKey() {
		return publicKeyCache;
	}

	@Override
	public void decode(ByteArray data, int incode) {
		int publicKeyIndex = 0;
		int privateKeyIndex = 0;
		// XOR
		for (int i = 0, len = data.length(); i < len; i++) {
			byte value = data.getByte(i);
			value ^= PRIVATE_KEY[privateKeyIndex++ % PRIVATE_KEY.length];
			value ^= public_key[publicKeyIndex++ % public_key.length];
			value ^= incode << 2;
			data.setByte(i, value);
		}

		// 备选方案
		// 首尾交换
		// 补码取反
	}
}