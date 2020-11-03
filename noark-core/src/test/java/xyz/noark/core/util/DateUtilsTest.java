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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;

import static org.junit.Assert.assertTrue;

/**
 * 时间工具类测试用例.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.2.4
 */
public class DateUtilsTest {

    @Test
    public void testFormatTime() {
        assertTrue("00:01:40".equals(DateUtils.formatTime(100)));
        assertTrue("00:01:00".equals(DateUtils.formatTime(60)));
        assertTrue("00:00:00".equals(DateUtils.formatTime(0)));
        assertTrue("00:00:00".equals(DateUtils.formatTime(-1)));
        assertTrue("100:00:00".equals(DateUtils.formatTime(360000)));
    }

    @Test
    public void testToDays() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
        {
            Date d1 = sdf.parse("2018-12-31 00:00:00:990");
            Date d2 = sdf.parse("2018-12-31 23:59:59:001");
            assertTrue(17896 == DateUtils.toDays(d1));
            assertTrue(17896 == DateUtils.toDays(d2));
        }

        {
            Date d1 = sdf.parse("2019-01-01 00:00:00:000");
            Date d2 = sdf.parse("2019-01-01 23:59:59:999");
            assertTrue(17897 == DateUtils.toDays(d1));
            assertTrue(17897 == DateUtils.toDays(d2));
        }
    }

    @Test
    public void testToSeconds() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
        Date d1 = sdf.parse("2018-12-12 23:59:59:888");
        Date d2 = sdf.parse("2018-12-13 00:00:00:888");
        Date d3 = sdf.parse("2018-12-13 00:00:01:888");
        assertTrue(DateUtils.toSeconds(d1) + 1 == DateUtils.toSeconds(d2));
        assertTrue(DateUtils.toSeconds(d2) + 1 == DateUtils.toSeconds(d3));
    }

    @Test
    public void testDiffDays() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
        {
            Date d1 = sdf.parse("2018-12-31 00:00:00:000");
            Date d2 = sdf.parse("2018-12-31 23:59:59:999");
            assertTrue(DateUtils.diffDays(d1, d2) == 0);
        }
        {
            Date d1 = sdf.parse("2018-12-30 00:00:00:000");
            Date d2 = sdf.parse("2018-12-31 23:59:59:999");
            assertTrue(DateUtils.diffDays(d1, d2) == -1);
        }
        {
            Date d1 = sdf.parse("2018-12-30 23:59:59:999");
            Date d2 = sdf.parse("2018-12-31 00:00:00:000");
            assertTrue(DateUtils.diffDays(d1, d2) == -1);
        }
    }

    @Test
    public void testToSecondsByStartOfDay() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
        Date d1 = sdf.parse("2019-02-14 00:00:00:000");
        assertTrue(DateUtils.toSeconds(d1) == DateUtils.toSecondsByStartOfDay(LocalDate.of(2019, 02, 14)));
    }

    @Test
    public void testGetStartOfDay() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
        Date d1 = sdf.parse("2019-02-14 00:00:00:000");
        Date d2 = sdf.parse("2019-02-14 01:02:03:004");
        assertTrue(DateUtils.getStartOfDay(d1).equals(DateUtils.getStartOfDay(d2)));
    }

    @Test
    public void testIsSameWeek() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
        Date d1 = sdf.parse("2019-12-30 00:00:00:000");
        Date d2 = sdf.parse("2020-01-01 00:00:00:000");
        assertTrue(DateUtils.isSameWeek(d1, d2));
    }
}