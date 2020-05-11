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

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * 字符串工具类测试用例.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.0
 */
public class StringUtilsTest {

    @Test
    public void testIsEmpty() {
        assertTrue(StringUtils.isEmpty(""));
        assertTrue(StringUtils.isEmpty(null));
        assertTrue(!StringUtils.isEmpty(" "));
        assertTrue(!StringUtils.isEmpty("test"));
        assertTrue(!StringUtils.isEmpty("  test "));
    }

    @Test
    public void testIsNotEmpty() {
        assertTrue(!StringUtils.isNotEmpty(""));
        assertTrue(!StringUtils.isNotEmpty(null));
        assertTrue(StringUtils.isNotEmpty(" "));
        assertTrue(StringUtils.isNotEmpty("test"));
        assertTrue(StringUtils.isNotEmpty("  test "));
    }

    @Test
    public void testLength() {
        assertTrue(StringUtils.length(null) == 0);
        assertTrue(StringUtils.length("") == 0);
        assertTrue(StringUtils.length(" ") == 1);
        assertTrue(StringUtils.length("test") == 4);
        assertTrue(StringUtils.length("我爱中国") == 8);
    }

    @Test
    public void testSplit() throws Exception {
        assertTrue(StringUtils.split("1,2,3", ",").length == 3);
        assertTrue(StringUtils.split("1,2,3,", ",").length == 3);
        assertTrue(StringUtils.split("1,2,3,,,", ",").length == 3);
        assertTrue(StringUtils.split("127.0.0.1", ".").length == 4);
    }
    
    @Test
    public void asciiSizeInBytes() throws Exception {
        long value = Long.MIN_VALUE;
        assertTrue(StringUtils.asciiSizeInBytes(value) == String.valueOf(value).length());
    }

    @Test
    public void testFormat() throws Exception {
        assertTrue("hahatrue,false,10000,false".equals(StringUtils.format("haha{1},{2},{0},{2}", 10000, true, false, 4)));
    }

    @Test
    public void leftPad() {
        assertEquals(StringUtils.leftPad(null, 5), null);
        assertEquals(StringUtils.leftPad("", 3), "   ");
        assertEquals(StringUtils.leftPad("bat", 3), "bat");
        assertEquals(StringUtils.leftPad("bat", 5), "  bat");
        assertEquals(StringUtils.leftPad("bat", 1), "bat");
        assertEquals(StringUtils.leftPad("bat", -1), "bat");
    }

    @Test
    public void testLeftPad() {
        assertEquals(StringUtils.leftPad(null, 1, ' '), null);
        assertEquals(StringUtils.leftPad("", 3, 'b'), "bbb");
        assertEquals(StringUtils.leftPad("bat", 3, 'b'), "bat");
        assertEquals(StringUtils.leftPad("bat", 5, ' '), "  bat");
        assertEquals(StringUtils.leftPad("bat", 1, 'b'), "bat");
        assertEquals(StringUtils.leftPad("bat", -1, 'b'), "bat");
    }
}