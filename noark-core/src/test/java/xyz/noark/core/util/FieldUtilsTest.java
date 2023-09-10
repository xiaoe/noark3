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

import org.junit.Test;
import xyz.noark.core.annotation.tpl.TplAttr;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

import static org.junit.Assert.*;

/**
 * 属性工具类测试.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.2
 */
public class FieldUtilsTest extends MethodUtilsTest {

    @TplAttr(name = "name")
    public static String name;

    @TplAttr(name = "test1")
    private int test1;

    @Test
    public void testWriteFieldObjectStringObject() {
        FieldUtilsTest test = new FieldUtilsTest();
        FieldUtils.writeField(test, "test1", 1);
        assertEquals(1, test.test1);

        FieldUtils.writeField(test, "test2", 2);
        assertEquals(2, test.getTest2());
    }

    @Test
    public void testGenGetMethodName() {
        assertEquals("getTest1", FieldUtils.genGetMethodName(Objects.requireNonNull(FieldUtils.getField(FieldUtilsTest.class, "test1"))));
    }

    @Test
    public void testGenSetMethodName() {
        assertEquals("setTest1", FieldUtils.genSetMethodName(Objects.requireNonNull(FieldUtils.getField(FieldUtilsTest.class, "test1"))));
    }

    @Test
    public void testScanAllField() {
        assertEquals(1, FieldUtils.scanAllField(FieldUtilsTest.class, Collections.singletonList(TplAttr.class)).length);
    }

    @Test
    public void testWriteFieldObjectFieldObject() {
        FieldUtilsTest test = new FieldUtilsTest();
        Field field = FieldUtils.getField(FieldUtilsTest.class, "test1");
        if (field != null) {
            FieldUtils.writeField(test, field, 2);
        }
        assertEquals(2, test.test1);
    }

    @Test
    public void testReadField() {
        FieldUtilsTest test = new FieldUtilsTest();
        assertEquals(0, FieldUtils.readField(test, Objects.requireNonNull(FieldUtils.getField(FieldUtilsTest.class, "test1"))));
    }

    @Test
    public void testGetField() {
        assertNotNull(FieldUtils.getField(FieldUtilsTest.class, "test1"));
        assertNotNull(FieldUtils.getField(FieldUtilsTest.class, "test2"));
        assertNull(FieldUtils.getField(FieldUtilsTest.class, "testx"));
    }

    @Test
    public void testGetAllField() {
        assertFalse(FieldUtils.getAllField(FieldUtilsTest.class).isEmpty());
    }

    @Test
    public void testInjectionStaticField() {
        Map<String, String> config = MapUtils.of("name", "haha");
        FieldUtils.injectionStaticField(config, FieldUtilsTest.class, v -> v);
        assertEquals("haha", FieldUtilsTest.name);
    }
}