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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * 随机工具类测试.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.0
 */
public class RandomUtilsTest {

    @Test
    public void testNextBoolean() {
        RandomUtils.nextBoolean();
    }

    @Test
    public void testNextIntInt() {
        assertEquals(RandomUtils.nextInt(1), 0);
    }

    @Test
    public void testNextIntIntInt() {
        assertEquals(RandomUtils.nextInt(1, 2), 1);
    }

    @Test
    public void testNextLongLong() {
        assertEquals(RandomUtils.nextLong(1), 0L);
    }

    @Test
    public void testNextLongLongLong() {
        assertEquals(RandomUtils.nextLong(1, 2), 1L);
    }

    @Test
    public void testIsSuccessFloat() {
        assertEquals(RandomUtils.isSuccess(0F), false);
        assertEquals(RandomUtils.isSuccess(1F), true);
    }

    @Test
    public void testIsSuccessDouble() {
        assertEquals(RandomUtils.isSuccess(0D), false);
        assertEquals(RandomUtils.isSuccess(1D), true);
    }

    @Test
    public void testIsSuccessByPercentage() {
        assertEquals(RandomUtils.isSuccessByPercentage(0), false);
        assertEquals(RandomUtils.isSuccessByPercentage(100), true);
    }

    @Test
    public void testIsSuccessByPermillage() {
        assertEquals(RandomUtils.isSuccessByPermillage(0), false);
        assertEquals(RandomUtils.isSuccessByPermillage(1000), true);
    }

    @Test
    public void testRandomList() {
        Object result = null;
        assertEquals(RandomUtils.randomList(null), result);

        List<Integer> list = new ArrayList<>();
        assertEquals(RandomUtils.randomList(list), null);
        assertEquals(RandomUtils.randomList(list, 0), Collections.emptyList());
        assertEquals(RandomUtils.randomList(null, 0), Collections.emptyList());

        list.add(1);
        assertEquals(RandomUtils.randomList(list), Integer.valueOf(1));
        assertEquals(RandomUtils.randomList(list, 1), Arrays.asList(1));

        list.add(2);
        assertEquals(RandomUtils.randomList(list, 1).size(), 1);
        assertEquals(RandomUtils.randomList(list, 2).size(), 2);
    }

    @Test
    public void testRandomByWeight() {
        List<TestData> data = new ArrayList<>();
        {
            TestData e = new TestData();
            e.setId(1);
            e.setWeight(0);
            data.add(e);
            TestData random = RandomUtils.randomByWeight(data, TestData::getWeight);
            assertEquals(random.getId(), e.getId());
        }

        {
            TestData e = new TestData();
            e.setId(1);
            e.setWeight(1);
            data.add(e);

            TestData random = RandomUtils.randomByWeight(data, TestData::getWeight);
            assertEquals(random.getId(), e.getId());
        }

        try {
            RandomUtils.randomByWeight(data, TestData::getWeigthx);
        } catch (Exception e) {
            assertNotNull(e);
        }
    }

    static class TestData {
        private int id;
        private int weight;
        private AtomicBoolean flag = new AtomicBoolean(true);

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getWeight() {
            return weight;
        }

        public void setWeight(int weight) {
            this.weight = weight;
        }

        public int getWeigthx() {
            // 特别的方法，实际上不会有这样的用法.
            if (flag.get()) {
                flag.set(false);
                return 1;
            } else {
                return -1;
            }
        }

        @Override
        public String toString() {
            return "TestData [id=" + id + ", weight=" + weight + "]";
        }
    }
}
