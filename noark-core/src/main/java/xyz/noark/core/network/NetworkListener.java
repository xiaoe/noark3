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

import xyz.noark.core.ioc.manager.PacketMethodManager;

/**
 * 网络监听接口.
 * <p>
 * 主要用于网络链接上的一些预处理.
 *
 * @since 3.0
 * @author 小流氓(176543888@qq.com)
 */
public interface NetworkListener {

	/**
	 * 链接刚刚链接成功.
	 * <p>
	 * 发给初始化封包
	 * 
	 * @param session Session对象
	 */
	void channelActive(Session session);

	/**
	 * 断开链接时调用.
	 * 
	 * @param session Session对象
	 */
	void channelInactive(Session session);

	/**
	 * 处理复制封包的逻辑.
	 * 
	 * @param session Session对象
	 * @param packet 网络封包
	 * @return 如果继续执行后面逻辑返回true.
	 */
	boolean handleDuplicatePacket(Session session, NetworkPacket packet);

	/**
	 * 处理篡改封包的逻辑.
	 * 
	 * @param session Session对象
	 * @param packet 网络封包
	 * @return 如果继续执行后面逻辑返回true.
	 */
	boolean handleChecksumFail(Session session, NetworkPacket packet);

	/**
	 * 处理过期或维护中的封包.
	 * <p>
	 * 给个提示<br>
	 * 临时关闭参考 {@link PacketMethodManager#temporarilyClosed(Serializable)}
	 * 
	 * @param session Session对象
	 * @param packet 网络封包
	 */
	void handleDeprecatedPacket(Session session, NetworkPacket packet);

	/**
	 * 处理封包统计预警功能.
	 * 
	 * @param session Session对象
	 * @param second 统计周期
	 * @param count 出现次数
	 * @param threshold 每秒累计长度阀值
	 * @return 如果继续执行后面逻辑返回true
	 */
	boolean handlePacketWarning(Session session, int second, int count, int threshold);

	/**
	 * 处理异常.
	 * <p>
	 * 所有逻辑执行的过程中如果抛出异常就会走这里
	 * 
	 * @param session Session对象
	 * @param packet 请求封包
	 * @param e 异常信息
	 */
	void handleException(Session session, NetworkPacket packet, Throwable e);
}