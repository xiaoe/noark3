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
package xyz.noark.network.filter;

import xyz.noark.core.annotation.Autowired;
import xyz.noark.core.annotation.Value;
import xyz.noark.core.network.NetworkListener;
import xyz.noark.core.network.NetworkPacket;
import xyz.noark.core.network.Session;
import xyz.noark.network.IncodeSession;
import xyz.noark.network.NetworkConstant;

import static xyz.noark.log.LogHelper.logger;

/**
 * 抽象封包检测过滤器.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.1
 */
public abstract class AbstractPacketCheckFilter implements PacketCheckFilter {

    /**
     * 网络安全之自增校验位检测：默认false不开启
     */
    @Value(NetworkConstant.INCODE)
    protected boolean incode = false;
    /**
     * 网络安全之checksum检测：默认false不开启
     */
    @Value(NetworkConstant.CHECKSUM)
    protected boolean checksum = false;

    @Autowired(required = false)
    protected NetworkListener networkListener;

    @Override
    public boolean checkIncode(IncodeSession session, NetworkPacket packet) {
        if (incode) {
            if (!checkPacketIncode(session, packet)) {
                logger.warn(" ^0^ duplicate packet. playerId={}, opcode={}", session.getPlayerId(), packet.getOpcode());
                if (networkListener != null) {
                    return networkListener.handleDuplicatePacket(session, packet);
                }
            }
        }
        return true;
    }

    /**
     * 检测封包自增校验位.
     *
     * @param session Session对象
     * @param packet  网络封包
     * @return 如果检测通过返回true
     */
    protected abstract boolean checkPacketIncode(IncodeSession session, NetworkPacket packet);

    @Override
    public boolean checkChecksum(Session session, NetworkPacket packet) {
        if (checksum) {
            if (!this.checkPacketChecksum(packet)) {
                logger.warn(" ^0^ checksum fail. playerId={}, opcode={}", session.getPlayerId(), packet.getOpcode());
                if (networkListener != null) {
                    return networkListener.handleChecksumFail(session, packet);
                }
            }
        }
        return true;
    }

    /**
     * 检测封包Checksum.
     *
     * @param packet 网络封包
     * @return 如果检测通过返回true
     */
    protected abstract boolean checkPacketChecksum(NetworkPacket packet);
}