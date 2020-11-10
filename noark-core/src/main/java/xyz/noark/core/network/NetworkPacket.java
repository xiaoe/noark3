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

import xyz.noark.core.lang.ByteArray;

import java.io.Serializable;

/**
 * 一个网络封包.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.0
 */
public interface NetworkPacket {

    /**
     * 获取封包长度.
     *
     * @return 封包长度
     */
    public int getLength();

    /**
     * 封包的编号.
     *
     * @return 编号
     */
    public Serializable getOpcode();

    /**
     * 封包的内容.
     *
     * @return 内容
     */
    public ByteArray getByteArray();

    /**
     * 获取自增校验位.
     * <p>
     * 一个有序自增的数值，可以用来判定是否为复制封包 <br>
     * 自增校验位，只是名称叫自增校验位，真实实现并不一定是一个有序自增的数值，只要是有规律的就行
     *
     * @return 自增校验位
     */
    public int getIncode();

    /**
     * 获取封包校验码.
     * <p>
     * 算是一种签名算法，正常使用CRC32，CRC16等等，主要用于防止篡改封包内容判定
     *
     * @return 封包校验码
     */
    public int getChecksum();
}