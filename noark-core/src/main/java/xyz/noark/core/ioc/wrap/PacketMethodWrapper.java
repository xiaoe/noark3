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
package xyz.noark.core.ioc.wrap;

import xyz.noark.core.network.NetworkPacket;
import xyz.noark.core.network.Session;

import java.io.Serializable;

/**
 * 封包方法入口的定义
 *
 * @author 小流氓[176543888@qq.com]
 */
public interface PacketMethodWrapper {

    /**
     * 获取封包编号.
     *
     * @return 封包编号
     */
    Serializable getOpcode();

    /**
     * 是否为远程封包
     *
     * @return 是否为远程封包
     */
    boolean isRemoteFlag();

    /**
     * 设置当前封包处理方法是否被废弃使用.
     *
     * @param deprecated 是否被废弃
     */
    void setDeprecated(boolean deprecated);

    /**
     * 判定当前封包处理方法是否被废弃使用.
     *
     * @return 如果被废弃返回true，否则返回false
     */
    boolean isDeprecated();

    /**
     * 把内容转成可直接阅读的信息
     *
     * @param session 链接Session
     * @param packet  封包内容
     * @return 返回这个封包可阅读字符串
     */
    String toString(Session session, NetworkPacket packet);


}
