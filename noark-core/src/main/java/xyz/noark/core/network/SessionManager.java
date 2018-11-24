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

import static xyz.noark.log.LogHelper.logger;

import java.io.Serializable;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

import xyz.noark.core.lang.ByteArray;
import xyz.noark.core.util.StringUtils;

/**
 * Session管理器.
 *
 * @since 3.0
 * @author 小流氓(176543888@qq.com)
 */
public class SessionManager {
	/** 所有链接服务器的会话. */
	private static final ConcurrentMap<Serializable, Session> SESSIONS = new ConcurrentHashMap<>(2048);
	/** 所有已进入游戏的会话. */
	private static final ConcurrentMap<Serializable, Session> PLAYER_ID_2_SESSION = new ConcurrentHashMap<>(2048);
	/** 账号与Session，用来顶号时踢除上一个链接 */
	private static final ConcurrentMap<Serializable, Session> UID_2_SESSION = new ConcurrentHashMap<>(2048);

	public static Session createSession(Serializable id, Function<Serializable, Session> mappingFunction) {
		return SESSIONS.computeIfAbsent(id, mappingFunction);
	}

	/**
	 * 群发封包.
	 * 
	 * @param opcode 协议编号
	 * @param protocal 协议对象
	 * @param playerIds 接受人的ID列表
	 */
	public static void send(Integer opcode, Object protocal, Serializable... playerIds) {
		ByteArray packet = PacketCodecHolder.getPacketCodec().encodePacket(new NetworkProtocal(opcode, protocal));
		// 全服发送
		if (playerIds.length == 0) {
			PLAYER_ID_2_SESSION.forEach((k, v) -> v.send(packet));
		} else {
			for (Serializable playerId : playerIds) {
				Session session = PLAYER_ID_2_SESSION.get(playerId);
				if (session == null) {
					logger.debug("未找到Session，无法发送, playerId={}", playerId);
				} else {
					session.send(packet);
				}
			}
		}
	}

	/**
	 * 当客户端断开后清理Session的方法.
	 * 
	 * @param session Session对象
	 */
	public static void removeSession(Session session) {
		SESSIONS.remove(session.getId());

		if (session.getPlayerId() != null) {
			PLAYER_ID_2_SESSION.remove(session.getPlayerId());
		}

		// ConcurrentHashMap是不可以删除Key为null的情况
		if (StringUtils.isNotEmpty(session.getUid())) {
			UID_2_SESSION.remove(session.getUid());
		}
	}

	/**
	 * 修复账号与Session新对应关系.
	 * 
	 * @param uid 账号
	 * @param session Session对象
	 * @return 如果原来有对应关系则返回老的Session，否返回Optional.empty()
	 */
	public static Optional<Session> setUidAndSession(String uid, Session session) {
		return Optional.ofNullable(UID_2_SESSION.put(uid, session));
	}

	/**
	 * 将玩家ID和Session对象绑定.
	 * 
	 * @param playerId 玩家ID
	 * @param session Session对象
	 */
	public static void bindPlayerIdAndSession(Serializable playerId, Session session) {
		PLAYER_ID_2_SESSION.put(playerId, session);
	}

	/**
	 * 根据链接ID来获取Session.
	 * 
	 * @param id 链接ID
	 * @return Session对象
	 */
	public static Session getSession(Serializable id) {
		return SESSIONS.get(id);
	}

	/**
	 * 根据玩家ID来获取Session对象.
	 * 
	 * @param playerId 玩家ID
	 * @return Session对象
	 */
	public static Session getSessionByPlayerId(Serializable playerId) {
		return PLAYER_ID_2_SESSION.get(playerId);
	}

	/**
	 * 获取所有在线玩家Session集合.
	 * 
	 * @return Session集合
	 */
	public static Collection<Session> getOnlineSessionList() {
		return PLAYER_ID_2_SESSION.values();
	}

	/**
	 * 获取所有在线玩家ID集合.
	 * 
	 * @return 玩家ID集合
	 */
	public static Set<Serializable> getOnlinePlayerIdList() {
		return PLAYER_ID_2_SESSION.keySet();
	}

	/**
	 * 判定一个玩家当前是否在线.
	 * 
	 * @param playerId 玩家ID
	 * @return 如果玩家在线则返回true,否则返回false.
	 */
	public static boolean isOnline(Serializable playerId) {
		return PLAYER_ID_2_SESSION.containsKey(playerId);
	}

	/**
	 * 统计当前在线玩家数量.
	 * 
	 * @return 在线玩家数量
	 */
	public static int statOnlinePlayerNum() {
		return PLAYER_ID_2_SESSION.size();
	}

	/**
	 * 统计当前在线账号数量.
	 * 
	 * @return 在线账号数量
	 */
	public static int statOnlineUidNum() {
		return UID_2_SESSION.size();
	}

	/**
	 * 统计当前链接数量.
	 * 
	 * @return 链接数量
	 */
	public static int statSessionNum() {
		return SESSIONS.size();
	}
}