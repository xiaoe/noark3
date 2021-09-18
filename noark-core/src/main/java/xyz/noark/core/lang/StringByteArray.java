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
package xyz.noark.core.lang;

import xyz.noark.core.util.StringUtils;

/**
 * 字符串类型的字节数组.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.4.3
 */
public class StringByteArray implements ByteArray {
    private final String text;

    /**
     * 缓存数据.
     */
    private byte[] cacheArray;

    public StringByteArray(String text) {
        this.text = text;
    }

    /**
     * 获取原生的Text.
     *
     * @return 原生的Text
     */
    public String getText() {
        return text;
    }

    @Override
    public byte[] array() {
        this.cacheArrayData();
        return cacheArray;
    }

    @Override
    public int length() {
        return text.length();
    }

    @Override
    public byte getByte(int index) {
        this.cacheArrayData();
        return cacheArray[index];
    }

    @Override
    public void setByte(int index, byte value) {
        this.cacheArrayData();
        cacheArray[index] = value;
    }

    private void cacheArrayData() {
        if (cacheArray == null) {
            this.cacheArray = StringUtils.utf8Bytes(text);
        }
    }

    @Override
    public void close() {
        // 当前数组内容不可变，所以不需要处理什么...
    }
}