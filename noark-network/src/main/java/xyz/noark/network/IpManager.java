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
package xyz.noark.network;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * IP管理器.
 * <p>
 * 主要用于限制相同IP最大链接数以及黑名单等功能
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.3.4
 */
public class IpManager {
    /**
     * IP统计计数
     */
    private static final Map<String, Integer> COUNTS = new ConcurrentHashMap<>();

    /**
     * 新激活一个通道
     *
     * @param ip 目标IP
     * @return 返回这个IP已激活的IP数量
     */
    public int active(String ip) {
        return COUNTS.merge(ip, 1, Integer::sum);
    }

    /**
     * 断开链接.
     * <p>
     * 释放这个IP计数
     *
     * @param ip 目标IP
     */
    public void inactive(String ip) {
        COUNTS.computeIfPresent(ip, (k, v) -> v > 1 ? v - 1 : null);
    }
}