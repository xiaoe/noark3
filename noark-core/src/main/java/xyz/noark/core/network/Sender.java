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
package xyz.noark.core.network;

import java.io.Serializable;

import xyz.noark.util.ArrayUtils;

/**
 * 网络层的封包发送工具类.
 *
 * @since 3.0
 * @author 小流氓(176543888@qq.com)
 */
public final class Sender {

	/**
	 * 给全服在线玩家转发一个封包.
	 * 
	 * @param opcode 协议编号
	 * @param protocal 协议对象
	 */
	public static void relayPacket(Integer opcode, Object protocal) {
		SessionManager.send(opcode, protocal);
	}

	/**
	 * 给指定玩家ID转发一个封包.
	 * 
	 * @param playerId 指定玩家的ID
	 * @param opcode 协议编号
	 * @param protocal 协议对象
	 */
	public static void relayPacket(Serializable playerId, Integer opcode, Object protocal) {
		SessionManager.send(opcode, protocal, playerId);
	}

	/**
	 * 给指定一群ID的玩家转发一个封包.
	 * <p>
	 * 如果roleIds为空则忽略本次发送，不然会发给全服玩家...
	 * 
	 * @param playerIds 指定的一群玩家ID
	 * @param opcode 封包操作码
	 * @param packet 封包对象
	 */
	public static void relayPacket(Serializable[] playerIds, Integer opcode, Object protocal) {
		if (ArrayUtils.isNotEmpty(playerIds)) {
			SessionManager.send(opcode, protocal, playerIds);
		}
	}

	/**
	 * 游戏服务器内部转发封包.
	 * <p>
	 * 主要用于跨服线程，穿透场景等...
	 * 
	 * @param roleId 指定玩家的ID
	 * @param opcode 封包操作码
	 * @param packet 封包对象
	 */
	public static void innerRelayPacket(long roleId, Object packet) {}
}