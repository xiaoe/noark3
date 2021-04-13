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
package xyz.noark.network.codec;

import xyz.noark.core.lang.ByteArray;
import xyz.noark.core.network.NetworkPacket;

import java.io.Serializable;

/**
 * 一种默认的网络封包结构.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.1
 */
public class DefaultNetworkPacket implements NetworkPacket {
    /**
     * 包长
     */
    private int length;
    /**
     * 协议编号
     */
    private Serializable opcode;
    /**
     * 协议内容
     */
    private ByteArray bytes;

    /**
     * 自增校验位
     */
    private int incode;
    /**
     * 封包CRC16
     */
    private int checksum;
    /**
     * 是否已解过码
     */
    private boolean decoded = false;

    @Override
    public Serializable getOpcode() {
        return opcode;
    }

    public void setOpcode(Serializable opcode) {
        this.opcode = opcode;
    }

    @Override
    public ByteArray getByteArray() {
        return bytes;
    }

    public void setBytes(ByteArray bytes) {
        this.bytes = bytes;
    }

    @Override
    public int getIncode() {
        return incode;
    }

    public void setIncode(int incode) {
        this.incode = incode;
    }

    @Override
    public int getChecksum() {
        return checksum;
    }

    public void setChecksum(int checksum) {
        this.checksum = checksum;
    }

    @Override
    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    @Override
    public boolean isDecoded() {
        return decoded;
    }

    @Override
    public void setDecoded(boolean decoded) {
        this.decoded = decoded;
    }

    @Override
    public String toString() {
        return "Packet [opcode=" + opcode + ", bytes=" + bytes + ", incode=" + incode + ", checksum=" + checksum + ", decoded=" + decoded + "]";
    }
}