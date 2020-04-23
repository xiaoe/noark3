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
package xyz.noark.core.util;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

/**
 * IP相关操作工具类.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.2
 */
public class IpUtils {

    /**
     * 私有IP：
     * <p>
     * A类 10.0.0.0-10.255.255.255 <br>
     * B类 172.16.0.0-172.31.255.255 <br>
     * C类 192.168.0.0-192.168.255.255<br>
     **/
    private static long A_BEGIN = ipToLong("10.0.0.0");
    private static long A_END = ipToLong("10.255.255.255");
    private static long B_BEGIN = ipToLong("172.16.0.0");
    private static long B_END = ipToLong("172.31.255.255");
    private static long C_BEGIN = ipToLong("192.168.0.0");
    private static long C_END = ipToLong("192.168.255.255");
    private static final int IP_LOOP_NUM = 3;
    private static final String LOCAL_IP = "127.0.0.1";

    /**
     * IP转化为Long类型的数字.
     *
     * @param ipAddress IP地址
     * @return Long类型的数字
     */
    public static long ipToLong(String ipAddress) {
        final String[] array = StringUtils.split(ipAddress, ".");
        long result = 0;
        for (int i = IP_LOOP_NUM; i >= 0; i--) {
            result |= Long.parseLong(array[3 - i]) << (i * 8);
        }
        return result;
    }

    /**
     * 判定一个IP是否为内网IP.
     * <p>
     * 除了上面的IP还包含了127.0.0.1
     *
     * @param ipAddress IP地址
     * @return 如果是则返回true, 否则返回false
     */
    public static boolean isInnerIp(String ipAddress) {
        final long ipNum = ipToLong(ipAddress);
        return isInner(ipNum, A_BEGIN, A_END) || isInner(ipNum, B_BEGIN, B_END) || isInner(ipNum, C_BEGIN, C_END) || LOCAL_IP.equals(ipAddress);
    }

    /**
     * 判定一个IP地址在否在指定区间内.
     *
     * @param ipNum IP地址
     * @param begin 指定区间开始值
     * @param end   指定区间结束值
     * @return 如果在区间内则返回true, 否则返回false
     */
    private static boolean isInner(long ipNum, long begin, long end) {
        return (ipNum >= begin) && (ipNum <= end);
    }

    /**
     * 获取套接字地址的IP
     *
     * @param address 套接字地址
     * @return 返回目标IP
     */
    public static String getIp(SocketAddress address) {
        return ((InetSocketAddress) address).getAddress().getHostAddress();
    }
}