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

import org.junit.BeforeClass;
import org.junit.Test;
import xyz.noark.benchmark.Benchmark;

import static org.junit.Assert.assertTrue;

/**
 * CRC32测试.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.1
 */
public class Crc32UtilsTest {
    private final static Benchmark BENCHMARK = new Benchmark(1000);

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    private static int crc32(byte[] buf) {
        java.util.zip.CRC32 crc = new java.util.zip.CRC32();
        crc.update(buf);
        return (int) crc.getValue();
    }

    @Test
    public void testCalculate() throws Exception {
        byte[] buf = new byte[]{0x00, 0x64, 0x01, 0x31, 0x31, (byte) 0xD1, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x03, 0x00, 0x00, 0x00, 0x00, 0x00, 0x16, 0x28, 0x23, 0x79, 0x65, 0x61, 0x72, 0x30, 0x5E, 0x30, 0x23, 0x29, 0x28, 0x40, 0x31, 0x40, 0x29, 0x31,
                0x31, 0x31, 0x31, 0x31, 0x31, 0x00, 0x00};

        assertTrue(Crc32Utils.calculate(buf) == crc32(buf));

        BENCHMARK.doSomething("Noark-CRC32:", () -> Crc32Utils.calculate(buf));
        BENCHMARK.doSomething("JDK-CRC32:", () -> crc32(buf));
        // 生成多项式为G(x)=x^8+x^2+x+1
    }
}