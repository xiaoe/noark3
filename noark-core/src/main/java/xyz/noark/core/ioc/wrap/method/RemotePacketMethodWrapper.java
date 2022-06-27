/*
 * Copyright © 2018 huiyunetwork.com All Rights Reserved.
 *
 * 感谢您加入辉娱网络，不用多久，您就会升职加薪、当上总经理、出任CEO、迎娶白富美、从此走上人生巅峰
 * 除非符合本公司的商业许可协议，否则不得使用或传播此源码，您可以下载许可协议文件：
 *
 * 		http://www.huiyunetwork.com/LICENSE
 *
 * 1、未经许可，任何公司及个人不得以任何方式或理由来修改、使用或传播此源码;
 * 2、禁止在本源码或其他相关源码的基础上发展任何派生版本、修改版本或第三方版本;
 * 3、无论你对源代码做出任何修改和优化，版权都归辉娱网络所有，我们将保留所有权利;
 * 4、凡侵犯辉娱网络相关版权或著作权等知识产权者，必依法追究其法律责任，特此郑重法律声明！
 */
package xyz.noark.core.ioc.wrap.method;

import xyz.noark.core.ioc.wrap.PacketMethodWrapper;
import xyz.noark.core.network.NetworkPacket;
import xyz.noark.core.network.Session;

import java.io.Serializable;

/**
 * 远程方法包装类.
 *
 * @author 小流氓[176543888@qq.com]
 */
public class RemotePacketMethodWrapper implements PacketMethodWrapper {
    private final String appGroup;
    private final int appId;

    private final Serializable opcode;
    /**
     * 当前方法是否已废弃使用.
     */
    protected boolean deprecated = false;

    public RemotePacketMethodWrapper(String appGroup, int appId, Serializable opcode) {
        this.appGroup = appGroup;
        this.appId = appId;
        this.opcode = opcode;
    }

    @Override
    public Serializable getOpcode() {
        return opcode;
    }

    @Override
    public boolean isRemoteFlag() {
        return true;
    }

    @Override
    public void setDeprecated(boolean deprecated) {
        this.deprecated = deprecated;
    }

    @Override
    public boolean isDeprecated() {
        return deprecated;
    }

    public String getAppGroup() {
        return appGroup;
    }

    public int getAppId() {
        return appId;
    }

    @Override
    public String toString(Session session, NetworkPacket packet) {
        StringBuilder sb = new StringBuilder(128);
        sb.append("[opcode=").append(opcode).append(',');
        sb.append("RemotePacket");
        sb.append(']');
        return sb.toString();
    }
}