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
package xyz.noark.core.util;

/**
 * 16进制工具类.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.4.7
 */
public class HexUtils {
    /**
     * 16进制显示字典.
     */
    private static final char[] HEX_DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    /**
     * 将一个字节数组转化为16进制显示的字符串
     *
     * @param array 一个字节数组
     * @return 返回16进制显示的字符串
     */
    public static String toHexString(byte[] array) {
        return toHexString(array, false);
    }

    /**
     * 将一个字节数组转化为16进制显示的字符串。
     *
     * @param array     一个字节数组
     * @param spaceFlag 是否需要空格分隔显示
     * @return 返回16进制显示的字符串
     */
    public static String toHexString(byte[] array, boolean spaceFlag) {
        // 没有分隔符的情况下就是长度*2
        int strLen = array.length >> 1;
        // 如果有分隔符，那还要增加长度-1
        if (spaceFlag) {
            strLen += array.length - 1;
        }

        StringBuilder sb = new StringBuilder(strLen);
        for (int i = 0, len = array.length; i < len; i++) {
            // 开始了，后面每一位都增加一个空格，方便阅读
            if (spaceFlag && i > 0) {
                sb.append(' ');
            }
            // 计算后字典取显示
            sb.append(HEX_DIGITS[(array[i] & 0xF0) >>> 4]);
            sb.append(HEX_DIGITS[(array[i] & 0x0F)]);
        }
        return sb.toString();
    }
}
