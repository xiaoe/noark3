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
 * 字节数组操作工具类.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.0
 */
public class ByteArrayUtils {
    /**
     * 一个空的字节数组.
     */
    public static final byte[] EMPTY_BYTE_ARRAY = {};

    /**
     * 一个short类型的数字转化为2位byte数组
     *
     * @param a short类型的数字
     * @return byte数组
     */
    public static byte[] toByteArray(short a) {
        return new byte[]{(byte) ((a >> 8) & 0xFF), (byte) (a & 0xFF)};
    }

    /**
     * 一个int类型的数字转化为4位byte数组
     *
     * @param num int类型的数字
     * @return byte数组
     */
    public static byte[] toByteArray(int num) {
        return new byte[]{(byte) ((num >> 24) & 0xFF), (byte) ((num >> 16) & 0xFF), (byte) ((num >> 8) & 0xFF), (byte) (num & 0xFF)};
    }

    /**
     * 4位byte数组转化为一个int类型的数字
     *
     * @param bytes byte数组
     * @return int类型的数字
     */
    public static int toInt(byte[] bytes) {
        return bytes[3] & 0xFF | (bytes[2] & 0xFF) << 8 | (bytes[1] & 0xFF) << 16 | (bytes[0] & 0xFF) << 24;
    }

    /**
     * N位byte数组读出一个无符号short类型的数字
     *
     * @param bytes byte数组
     * @return short类型的数字
     */
    public static int toUnsignedShort(byte[] bytes) {
        return toUnsignedShort(bytes, 0);
    }

    /**
     * N位byte数组读出一个无符号short类型的数字
     *
     * @param bytes byte数组
     * @param off   偏移值
     * @return short类型的数字
     */
    public static int toUnsignedShort(byte[] bytes, int off) {
        return (bytes[off + 1] << 8 & 0xFF00) | (bytes[off] & 0xFF);
    }
}