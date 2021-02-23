package xyz.noark.core.util;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertArrayEquals;


/**
 * ZIP压缩测试
 *
 * @author 小流氓[176543888@qq.com]
 */
public class ZipUtilsTest {
    @Test
    public void testCompress() throws IOException {
        byte[] data = "中文中文中文中文中文中文中文中文中文中文中文中文中文中文中文中文中文中文中文中文中文中文中文中文中文中文中文中文中文中文中文中文中文中文中文中文中文中文中文中文中文中文中文中文中文中文中文中文中文中文中文中文中文中文中文中文中文中文中文中文中文中文中文中文中文中文中文中文中文中文中文中文中文中文中文".getBytes(StandardCharsets.UTF_8);
        byte[] result = ZipUtils.compress(data);
        assertArrayEquals(data, ZipUtils.uncompress(result));
    }
}