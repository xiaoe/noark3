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
package xyz.noark.core.network;

/**
 * 封包统计数据.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.1
 */
public class PacketStatistics {
    /**
     * 上次记录时间
     */
    private long lastRecordSecond;
    /**
     * 累计长度
     */
    private int packetLength;

    /**
     * 上次预警时间
     */
    private long lastWarnSecond;
    /**
     * 已预警次数
     */
    private int warnCount;

    /**
     * 记录数据.
     *
     * @param second 当前秒
     * @param length 封包长度
     * @return 当前秒内累计接受到的封包长度
     */
    public long record(long second, int length) {
        // 当前与上次时间处于同一秒内,累加长度
        if (lastRecordSecond == second) {
            this.packetLength += length;
        }
        // 已超时，重新记录
        else {
            this.lastRecordSecond = second;
            this.packetLength = length;
        }
        return packetLength;
    }

    /**
     * 预警记录.
     *
     * @param second           当前秒
     * @param statisticalCycle 统计周期为多少秒
     * @return 当前秒已预警次数
     */
    public int warning(long second, int statisticalCycle) {
        // 统计周期内
        if (lastWarnSecond + statisticalCycle >= second) {
            this.warnCount++;
        }
        // 已超时
        else {
            this.lastWarnSecond = second;
            this.warnCount = 1;
        }
        return warnCount;
    }
}