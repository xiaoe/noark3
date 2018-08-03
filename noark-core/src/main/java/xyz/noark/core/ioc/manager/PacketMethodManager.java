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
package xyz.noark.core.ioc.manager;

import static xyz.noark.log.LogHelper.logger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import xyz.noark.core.ioc.wrap.method.PacketMethodWrapper;

/**
 * 封包方法管理类.
 *
 * @since 3.0
 * @author 小流氓(176543888@qq.com)
 */
public class PacketMethodManager {
	private final ConcurrentMap<Integer, PacketMethodWrapper> handlers = new ConcurrentHashMap<>(2048);
	private static final PacketMethodManager INSTANCE = new PacketMethodManager();

	private PacketMethodManager() {}

	public static PacketMethodManager getInstance() {
		return INSTANCE;
	}

	public void resetPacketHandler(PacketMethodWrapper handler) {
		// 如果没有完成初始化判定一下 会不会有重复的Opcode
		if (handlers.containsKey(handler.getOpcode())) {
			throw new RuntimeException("重复定义的 Opcode：" + handler.getOpcode());
		}
		handlers.put(handler.getOpcode(), handler);
	}

	public PacketMethodWrapper getPacketMethodWrapper(Integer opcode) {
		return handlers.get(opcode);
	}

	/**
	 * 临时关闭协议的入口.
	 * <p>
	 * 当XX模块发生了Bug时，可临时关闭此功能入口<br>
	 * 也只有协议编号不存在时才会返回失败吧...<br>
	 * 
	 * @param opcode 协议编号
	 * @return 如果关闭成功返回true,否则返回false.
	 */
	public boolean temporarilyClosed(Integer opcode) {
		PacketMethodWrapper method = this.getPacketMethodWrapper(opcode);
		if (method == null) {
			return false;
		}
		method.setDeprecated(true);
		return true;
	}

	/**
	 * 临时开启协议的入口.
	 * <p>
	 * 当XX模块修复了Bug时，可临时开启此功能入口<br>
	 * 也只有协议编号不存在时才会返回失败吧...<br>
	 * 
	 * @param opcode 协议编号
	 * @return 如果开启成功返回true,否则返回false.
	 */
	public boolean temporaryOpening(Integer opcode) {
		PacketMethodWrapper method = this.getPacketMethodWrapper(opcode);
		if (method == null) {
			return false;
		}
		method.setDeprecated(false);
		return true;
	}

	public void outputStatInfo() {
		for (Map.Entry<Integer, PacketMethodWrapper> e : handlers.entrySet()) {
			final long num = e.getValue().getCallNum();
			if (num > 0) {
				logger.info("protocal stat. opcode={}, call={}", e.getKey(), num);
			}
		}
	}
}