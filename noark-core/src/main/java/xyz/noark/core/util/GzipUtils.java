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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * GZip压缩工具类.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.4
 */
public class GzipUtils {
    /**
     * GZip压缩编码
     */
    public static final String ENCODING_GZIP = "gzip";

    /**
     * 判定指定数据是否Gzip压缩数据.
     *
     * @param data 指定数据
     * @return 如果是Gzip压缩数据则返回true，否则返回false
     */
    public static boolean isGzip(byte[] data) {
        // 如果数据为空或长度小于2位的情况，肯定不是Gzip压缩方式
        if (data == null || data.length <= 1) {
            return false;
        }
        return ByteArrayUtils.toUnsignedShort(data) == GZIPInputStream.GZIP_MAGIC;
    }

    /**
     * 以GZip方式压缩
     *
     * @param data 要压缩的数据
     * @return 压缩后的数据
     * @throws IOException 如果发生I/O错误。
     */
    public static byte[] compress(byte[] data) throws IOException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length)) {
            // 写法有点怪，没有关系，GZip压缩写得怪，请参考java.util.zip.GZIPOutputStream.finish()
            try (GZIPOutputStream gzipOutputStream = new GZIPOutputStream(outputStream)) {
                gzipOutputStream.write(data);
            }
            return outputStream.toByteArray();
        }
    }

    /**
     * 以GZip方式解压
     *
     * @param data 要压缩的数据
     * @return 解压后的数据
     * @throws IOException 如果发生I/O错误。
     */
    public static byte[] uncompress(byte[] data) throws IOException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
             GZIPInputStream gzipInputStream = new GZIPInputStream(new ByteArrayInputStream(data))) {
            int n;
            byte[] temp = new byte[1024];
            while ((n = gzipInputStream.read(temp)) > 0) {
                outputStream.write(temp, 0, n);
            }
            return outputStream.toByteArray();
        }
    }
}