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
