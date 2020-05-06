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

import java.io.ByteArrayOutputStream;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

/**
 * ZLib压缩工具
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.0
 */
public class ZlibUtils {
    /**
     * 压缩字节数组.
     *
     * @param array 将要压缩字节数组
     * @return 压缩后的字节数组
     */
    public static byte[] compress(byte[] array) {
        final Deflater deflater = new Deflater();
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream(array.length)) {
            deflater.setInput(array);
            deflater.finish();
            byte[] buf = new byte[1024];
            while (!deflater.finished()) {
                baos.write(buf, 0, deflater.deflate(buf));
            }
            return baos.toByteArray();
        } catch (Exception e) {
            // 压缩失败，那就不压了嘛...
            return array;
        } finally {
            deflater.end();
        }
    }

    /**
     * 解压缩.
     *
     * @param data 待解压的数据
     * @return 解压缩后的数据
     */
    public static byte[] uncompress(byte[] data) {
        final Inflater decompresser = new Inflater();
        try (ByteArrayOutputStream o = new ByteArrayOutputStream(data.length)) {
            decompresser.reset();
            decompresser.setInput(data);

            byte[] buf = new byte[1024];
            while (!decompresser.finished()) {
                o.write(buf, 0, decompresser.inflate(buf));
            }
            return o.toByteArray();
        } catch (Exception e) {
            // 解不开那就不解了...
            return data;
        } finally {
            decompresser.end();
        }
    }
}
