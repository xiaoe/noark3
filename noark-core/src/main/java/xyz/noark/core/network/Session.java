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

import xyz.noark.core.lang.ByteArray;

/**
 * Session.
 *
 * @since 3.0
 * @author 小流氓[176543888@qq.com]
 */
public interface Session extends SessionAttrMap {

	/**
	 * 获取Session的ID.
	 * 
	 * @return 实际就是链接Channel的ID
	 */
	public Serializable getId();

	/**
	 * 获取当前Session链接的IP地址
	 * 
	 * @return IP地址
	 */
	public String getIp();

	/**
	 * 关闭当前Session的链接.
	 */
	public void close();

	/**
	 * 发送一个网络封包.
	 * 
	 * @param opcode 协议编号
	 * @param protocol 协议对象
	 */
	public void send(Serializable opcode, Object protocol);

	/**
	 * 发送完成后关闭当前链接.
	 * 
	 * @param opcode 协议编号
	 * @param protocol 协议对象
	 */
	public void sendAndClose(Serializable opcode, Object protocol);

	/**
	 * 发送一个网络封包.
	 * <p>
	 * 封包是已处理过的加密压缩等功能后的包
	 * 
	 * @param packet 封包内容
	 */
	public void send(ByteArray packet);

	/**
	 * 发送一个网络协议
	 * 
	 * @param networkProtocol 网络协议
	 */
	public void send(NetworkProtocol networkProtocol);

	/**
	 * 获取当前链接状态.
	 * 
	 * @return 链接状态
	 */
	public State getState();

	/**
	 * 获取玩家的UID.
	 * 
	 * @return 玩家的UID
	 */
	public String getUid();

	/**
	 * 获取玩家ID
	 * 
	 * @return 玩家ID
	 */
	public Serializable getPlayerId();

	/**
	 * 清除账号和角色ID，用于顶号后解绑
	 */
	public void clearUidAndPlayerId();

	/**
	 * 获取封包统计情况.
	 * 
	 * @return 封包统计情况
	 */
	public PacketStatis getStatis();

	/**
	 * 获取封包加密接口.
	 * 
	 * @return 封包加密接口
	 */
	public PacketEncrypt getPacketEncrypt();

	/**
	 * Session状态.
	 */
	static enum State {
		/**
		 * 随便什么状态都可以访问.
		 * <p>
		 * 状态比较特殊，比如心跳
		 */
		ALL,
		/**
		 * 客户端刚刚链接上来的状态.
		 * <p>
		 * 未登录才可以调用的方法
		 */
		CONNECTED,
		/**
		 * 已认证状态.
		 * <p>
		 * 已登录但未进游戏（可能有选角界面）
		 */
		AUTHENTICATED,
		/**
		 * 游戏中状态，选择角色进入游戏了.
		 */
		INGAME;
	}
}