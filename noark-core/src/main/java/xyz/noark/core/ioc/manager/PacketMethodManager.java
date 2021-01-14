/*
 * Copyright © 2018 www.noark.xyz All Rights Reserved.
 *
 * 感谢您选择Noark框架，希望我们的努力能为您提供一个简单、易用、稳定的服务器端框架 ！
 * 除非符合Noark许可协议，否则不得使用该文件，您可以下载许可协议文件：
 *
 *        http://www.noark.xyz/LICENSE
 *
 * 1.未经许可，任何公司及个人不得以任何方式或理由对本框架进行修改、使用和传播;
 * 2.禁止在本项目或任何子项目的基础上发展任何派生版本、修改版本或第三方版本;
 * 3.无论你对源代码做出任何修改和改进，版权都归Noark研发团队所有，我们保留所有权利;
 * 4.凡侵犯Noark版权等知识产权的，必依法追究其法律责任，特此郑重法律声明！
 */
package xyz.noark.core.ioc.manager;

import xyz.noark.core.exception.ServerBootstrapException;
import xyz.noark.core.ioc.wrap.method.PacketMethodWrapper;
import xyz.noark.core.network.NetworkPacket;
import xyz.noark.core.network.Session;
import xyz.noark.core.util.StringUtils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static xyz.noark.log.LogHelper.logger;

/**
 * 封包方法管理类.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.0
 */
public class PacketMethodManager {
    private static final PacketMethodManager INSTANCE = new PacketMethodManager();
    private final ConcurrentMap<Serializable, PacketMethodWrapper> handlers = new ConcurrentHashMap<>(2048);

    private PacketMethodManager() {
    }

    public static PacketMethodManager getInstance() {
        return INSTANCE;
    }

    public void resetPacketHandler(PacketMethodWrapper handler) {
        // 如果没有完成初始化判定一下 会不会有重复的Opcode
        if (handlers.containsKey(handler.getOpcode())) {
            throw new ServerBootstrapException("重复定义的 Opcode：" + handler.getOpcode());
        }
        handlers.put(handler.getOpcode(), handler);
    }

    public PacketMethodWrapper getPacketMethodWrapper(Serializable opcode) {
        return handlers.get(opcode);
    }

    /**
     * 临时关闭协议的入口.
     * <p>
     * 当XX模块发生了Bug时，可临时关闭此功能入口<br>
     * 也只有协议编号不存在时才会返回失败吧...<br>
     *
     * @param opcode 协议编号
     * @return 如果关闭成功返回true, 否则返回false.
     */
    public boolean temporarilyClosed(Serializable opcode) {
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
     * @return 如果开启成功返回true, 否则返回false.
     */
    public boolean temporaryOpening(Serializable opcode) {
        PacketMethodWrapper method = this.getPacketMethodWrapper(opcode);
        if (method == null) {
            return false;
        }
        method.setDeprecated(false);
        return true;
    }

    /**
     * 输出统计信息
     *
     * @param maxSize TopN
     */
    public void outputStatInfo(int maxSize) {
        Map<Serializable, Long> result = new HashMap<>(handlers.size());
        for (Map.Entry<Serializable, PacketMethodWrapper> e : handlers.entrySet()) {
            final long num = e.getValue().getCallNum();
            if (num <= 0) {
                continue;
            }
            result.put(e.getKey(), num);
        }
        // 排序后只输出前多少个.
        result.entrySet().stream().sorted(Map.Entry.<Serializable, Long>comparingByValue().reversed()).limit(maxSize).forEachOrdered(e -> {
            logger.info("protocol stat. opcode={}, call={}", e.getKey(), e.getValue());
        });
    }

    /**
     * 记录封包信息.
     *
     * @param session 链接Session
     * @param packet  封包内容
     */
    public void logPacket(Session session, NetworkPacket packet) {
        logger.warn("^(oo)^ packet info. session={}, packet={}", analysisSession(session), analysisPacket(session, packet));
    }

    private Object analysisSession(Session session) {
        if (session == null) {
            return "null";
        }

        return session.getId();
    }

    private String analysisPacket(Session session, NetworkPacket packet) {
        PacketMethodWrapper pmw = INSTANCE.getPacketMethodWrapper(packet.getOpcode());
        if (pmw == null) {
            return StringUtils.join("illegal opcode:", packet.getOpcode().toString());
        }
        // 把内容转成可直接阅读的信息
        return pmw.toString(session, packet);
    }
}