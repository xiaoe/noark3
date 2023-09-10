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
package xyz.noark.core.lang;

import org.junit.Test;
import xyz.noark.core.util.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * LocalTime数组测试用例
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.3.9
 */
public class LocalTimeArrayTest {

    @Test
    public void testDoNextLocalTime() {
        List<LocalTime> times = new ArrayList<>();
        times.add(LocalTime.of(8, 0));
        times.add(LocalTime.of(12, 0));
        times.add(LocalTime.of(20, 0));
        LocalTimeArray array = new LocalTimeArray(times.toArray(new LocalTime[]{}));

        // 今天的开始时间
        final Date todayStartTime = new Date(DateUtils.toSecondsByStartOfDay(LocalDate.now()) * 1000);

        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        {// 假如当前是0点，应该返回是明天早上0点
            LocalTime now = LocalTime.of(0, 0, 0);
            assertEquals(sdf.format(DateUtils.addHours(todayStartTime, 8)), sdf.format(array.doNext(now)));
        }
        {// 假如当前是8点，应该是返回12点的时间
            LocalTime now = LocalTime.of(8, 0, 0);
            assertEquals(sdf.format(DateUtils.addHours(todayStartTime, 12)), sdf.format(array.doNext(now)));
        }
        {// 假如当前是23点，应该返回是明天早上8点
            LocalTime now = LocalTime.of(23, 59, 59);
            assertEquals(sdf.format(DateUtils.addHours(todayStartTime, 32)), sdf.format(array.doNext(now)));
        }
        {// 假如当前是23点，应该返回是明天早上8点
            LocalTime now = LocalTime.of(23, 59, 59);
            LocalDate start = LocalDate.of(2020, 1, 1);
            assertEquals(sdf.format(DateUtils.addHours(todayStartTime, 32)), sdf.format(array.doNext(start, now)));
        }
    }

    @Test
    public void testTriggerTimes() throws ParseException {
        List<LocalTime> times = new ArrayList<>();
        times.add(LocalTime.of(8, 0));
        times.add(LocalTime.of(12, 0));
        times.add(LocalTime.of(20, 0));
        LocalTimeArray array = new LocalTimeArray(times.toArray(new LocalTime[]{}));

        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        final Date now = sdf.parse("2019-12-12 12:00:00");

        {// 触发时间在当前时间之后
            final Date lastTriggerTime = sdf.parse("2019-12-13 12:00:00");
            assertEquals(0, array.triggerTimes(lastTriggerTime, now));
        }
        {
            final Date lastTriggerTime = sdf.parse("2019-12-12 12:00:00");
            assertEquals(0, array.triggerTimes(lastTriggerTime, now));
        }
        {
            final Date lastTriggerTime = sdf.parse("2019-12-12 11:00:00");
            assertEquals(1, array.triggerTimes(lastTriggerTime, now));
        }
        {
            final Date lastTriggerTime = sdf.parse("2019-12-12 00:00:00");
            assertEquals(2, array.triggerTimes(lastTriggerTime, now));
        }
        {
            final Date lastTriggerTime = sdf.parse("2019-12-11 23:59:59");
            assertEquals(2, array.triggerTimes(lastTriggerTime, now));
        }

        {
            final Date lastTriggerTime = sdf.parse("2019-12-10 23:59:59");
            assertEquals(5, array.triggerTimes(lastTriggerTime, now));
        }
        {
            final Date lastTriggerTime = sdf.parse("2019-12-01 08:00:00");
            assertEquals(34, array.triggerTimes(lastTriggerTime, now));
        }
    }

    @Test
    public void testDoNext() {
        List<LocalTime> times = new ArrayList<>();
        times.add(LocalTime.of(23, 59, 59));
        LocalTimeArray array = new LocalTimeArray(times.toArray(new LocalTime[]{}));

        LocalTime now = LocalTime.of(0, 0, 0);
        LocalDateTime want = LocalDate.now().atTime(LocalTime.of(23, 59, 59));
        assertEquals(want, DateUtils.toLocalDateTime(array.doNext(now)));
    }
}