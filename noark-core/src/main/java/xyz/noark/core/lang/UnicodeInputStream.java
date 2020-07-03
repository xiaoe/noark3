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
package xyz.noark.core.lang;


import xyz.noark.core.util.IntUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;

/**
 * UnicodeInputStream将识别Unicode的BOM标记.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.4
 */
public class UnicodeInputStream extends InputStream {
    private static final int BOM_SIZE = 4;
    private static final byte BOM_0X00 = 0x00;
    private static final byte BOM_0XFF = (byte) 0xFF;
    private static final byte BOM_0XFE = (byte) 0xFE;
    private static final byte BOM_0XEF = (byte) 0xEF;
    private static final byte BOM_0XBB = (byte) 0xBB;
    private static final byte BOM_0XBF = (byte) 0xBF;

    private final PushbackInputStream pis;
    private final String defaultEncoding;
    private String encoding;

    public UnicodeInputStream(InputStream in, String defaultEncoding) throws IOException {
        this.pis = new PushbackInputStream(in, BOM_SIZE);
        this.defaultEncoding = defaultEncoding;
        this.pretreatment();
    }

    /**
     * 获取分析出来的编码方式
     *
     * @return 编码方式
     */
    public String getEncoding() {
        return encoding;
    }

    /**
     * 预处理四个字节并检查BOM标记
     */
    protected void pretreatment() throws IOException {
        byte[] bom = new byte[BOM_SIZE];
        int n, unread;
        n = pis.read(bom, 0, bom.length);

        if ((bom[0] == BOM_0X00) && (bom[1] == BOM_0X00) && (bom[IntUtils.NUM_2] == BOM_0XFE) && (bom[IntUtils.NUM_3] == BOM_0XFF)) {
            encoding = "UTF-32BE";
            unread = n - 4;
        } else if ((bom[0] == BOM_0XFF) && (bom[1] == BOM_0XFE) && (bom[IntUtils.NUM_2] == BOM_0X00) && (bom[IntUtils.NUM_3] == BOM_0X00)) {
            encoding = "UTF-32LE";
            unread = n - 4;
        } else if ((bom[0] == BOM_0XEF) && (bom[1] == BOM_0XBB) && (bom[IntUtils.NUM_2] == BOM_0XBF)) {
            encoding = "UTF-8";
            unread = n - 3;
        } else if ((bom[0] == BOM_0XFE) && (bom[1] == BOM_0XFF)) {
            encoding = "UTF-16BE";
            unread = n - 2;
        } else if ((bom[0] == BOM_0XFF) && (bom[1] == BOM_0XFE)) {
            encoding = "UTF-16LE";
            unread = n - 2;
        } else {
            encoding = defaultEncoding;
            unread = n;
        }
        if (unread > 0) {
            pis.unread(bom, (n - unread), unread);
        }
    }

    @Override
    public int read() throws IOException {
        return pis.read();
    }

    @Override
    public void close() throws IOException {
        pis.close();
    }
}