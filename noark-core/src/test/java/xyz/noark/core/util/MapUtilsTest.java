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

import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Map工具类测试用例.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.2.4
 */
public class MapUtilsTest {

    /**
     * 构建一个测试Map数据
     */
    private Map<Integer, Integer> testMap = MapUtils.of(1, 2);
    private Map<Integer, Long> ratioMap = new HashMap<>(16);

    @Before
    public void setUp() throws Exception {
        ratioMap.put(1, 1000L);
        ratioMap.put(2, 2000L);
        ratioMap.put(3, 3000L);
        ratioMap.put(4, 4000L);
        ratioMap.put(5, 5000L);
    }

    @Test
    public void testIsEmpty() {
        assertTrue(MapUtils.isEmpty(null));
        assertTrue(MapUtils.isEmpty(Collections.emptyMap()));
        assertFalse(MapUtils.isEmpty(testMap));
    }

    @Test
    public void testIsNotEmpty() {
        assertFalse(MapUtils.isNotEmpty(null));
        assertFalse(MapUtils.isNotEmpty(Collections.emptyMap()));
        assertTrue(MapUtils.isNotEmpty(testMap));
    }

    @Test
    public void testOf() {
        assertEquals(null, testMap.get(2));
        assertNotEquals(null, testMap.get(1));
        assertEquals(Integer.valueOf(2), testMap.get(1));
    }

    @Test
    public void testGetOrMaxKey() {
        Map<Integer, Integer> map = new HashMap<>(16);
        map.put(1, 10);
        map.put(2, 15);
        map.put(3, 20);
        map.put(4, 25);
        map.put(5, 30);
        map.put(10, 100);

        assertEquals(MapUtils.getOrMaxKey(map, 1), Integer.valueOf(10));
        assertEquals(MapUtils.getOrMaxKey(map, 2), Integer.valueOf(15));
        assertEquals(MapUtils.getOrMaxKey(map, 4), Integer.valueOf(25));
        assertEquals(MapUtils.getOrMaxKey(map, 5), Integer.valueOf(30));
        assertEquals(MapUtils.getOrMaxKey(map, 6), Integer.valueOf(30));
        assertEquals(MapUtils.getOrMaxKey(map, 12), Integer.valueOf(100));
    }

    @Test
    public void testAddByIntValue() {
        Map<Integer, Integer> source = MapUtils.of(1, 2);
        MapUtils.addByIntValue(source, null);
        MapUtils.addByIntValue(source, source);
        assertEquals(source.get(1), Integer.valueOf(4));
    }

    @Test
    public void testAddByLongValue() {
        Map<Integer, Long> source = MapUtils.of(1, 2L);
        MapUtils.addByLongValue(source, null);
        MapUtils.addByLongValue(source, source);
        assertEquals(source.get(1), Long.valueOf(4));
    }

    @Test
    public void testAddValueMapOfInt() {
        Map<Integer, Integer> source = new HashMap<>(16);
        MapUtils.addValue(source, 1, 1);
        assertEquals(source.get(1), Integer.valueOf(1));
        MapUtils.addValue(source, 1, 1);
        assertEquals(source.get(1), Integer.valueOf(2));
    }

    @Test
    public void testAddValueMapOfLong() {
        Map<Integer, Long> source = new HashMap<>(16);
        MapUtils.addValue(source, 1, 1L);
        assertEquals(source.get(1), Long.valueOf(1));
        MapUtils.addValue(source, 1, 1L);
        assertEquals(source.get(1), Long.valueOf(2));
    }

    @Test
    public void testAddValueMapOfFloat() {
        Map<Integer, Float> source = new HashMap<>(16);
        MapUtils.addValue(source, 1, 1F);
        assertEquals(source.get(1), Float.valueOf(1));
        MapUtils.addValue(source, 1, 1F);
        assertEquals(source.get(1), Float.valueOf(2));
    }

    @Test
    public void testAddValueMapOfDouble() {
        Map<Integer, Double> source = new HashMap<>(16);
        MapUtils.addValue(source, 1, 1D);
        assertEquals(source.get(1), Double.valueOf(1));
        MapUtils.addValue(source, 1, 1D);
        assertEquals(source.get(1), Double.valueOf(2));
    }

    @Test
    public void testGetRatioValue() {
        double result = MapUtils.getRatioValue(ratioMap, MathUtils.TEN_THOUSAND, Integer.valueOf(1), Integer.valueOf(4));
        assertEquals(Double.valueOf(result), Double.valueOf(0.5));
    }

    @Test
    public void testGetPermillageValue() {
        double result = MapUtils.getPermillageValue(ratioMap, Integer.valueOf(1), Integer.valueOf(4), Integer.valueOf(10));
        assertEquals(Double.valueOf(result), Double.valueOf(5));
    }

    @Test
    public void testGetPercentageValue() {
        double result = MapUtils.getPercentageValue(ratioMap, Integer.valueOf(1), Integer.valueOf(4), Integer.valueOf(10));
        assertEquals(Double.valueOf(result), Double.valueOf(50));
    }
}