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

import xyz.noark.core.exception.ConvertException;
import xyz.noark.core.util.DateUtils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

/**
 * LocalTime数组.
 * <p>
 * 主要用于策划想定时触发指定功能时好配置，如:
 *
 * <pre>
 * 08:00:00,12:00:00,18:00:00<br>
 * 08:00,12:00,18:00<br>
 * </pre>
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.3.9
 */
public class LocalTimeArray {
    /**
     * 每天最大的秒数
     */
    private static final int MAX_SECOND_BY_DAY = 24 * 60 * 60;

    private final LocalTime[] array;

    public LocalTimeArray(LocalTime[] array) {
        this.array = array;
    }

    public LocalTime[] getArray() {
        return array;
    }

    /**
     * 以当前时间算，计算出下次触发时间
     *
     * @return 下次触发时间
     */
    public Date doNext() {
        return doNext(LocalTime.now());
    }

    /**
     * 以指定时间算，计算出下次触发时间
     *
     * @param now 指定时间
     * @return 下次触发时间
     */
    public Date doNext(LocalTime now) {
        return doNext(null, now);
    }

    /**
     * 以当前时间算，计算出指定开始日期后第一次触发时间
     *
     * @param start 指定开始日期
     * @return 下次触发时间
     */
    public Date doNext(LocalDate start) {
        return doNext(start, LocalTime.now());
    }

    /**
     * 以指定时间算，计算出下次触发时间
     *
     * @param start 指定开始日期
     * @param now   指定时间
     * @return 下次触发时间
     */
    public Date doNext(LocalDate start, LocalTime now) {
        // 有指定开始日期且在今天之后
        boolean flag = start != null && start.isAfter(LocalDate.now());

        // 所有节点中最小的那个节点，用于计算跨天的时间
        int minSecond = Integer.MAX_VALUE;
        // 推算出节点中在当前时间之后的那个节点所距的秒数
        boolean hasNext = false;
        int nextSecond = Integer.MAX_VALUE;

        // 当前时间对应的今天第几秒
        final int nowTodaySecond = now.toSecondOfDay();
        for (LocalTime node : array) {
            // 每一个节点时间对应的今天第几秒
            int nodeTodaySecond = node.toSecondOfDay();
            minSecond = Math.min(minSecond, nodeTodaySecond);

            // 在当前时间之后的那个节点
            if (!flag && nodeTodaySecond > nowTodaySecond) {
                hasNext = true;
                nextSecond = Math.min(nextSecond, nodeTodaySecond - nowTodaySecond);
            }
        }

        // 没有下个节点，那就计算过天的下个节点时间
        if (!hasNext) {
            nextSecond = MAX_SECOND_BY_DAY - nowTodaySecond + minSecond;
        }

        Calendar calendar = Calendar.getInstance();
        // 对日期有要求的
        if (flag) {
            calendar.set(Calendar.YEAR, start.getYear());
            calendar.set(Calendar.MONTH, start.getMonthValue() - 1);
            calendar.set(Calendar.DAY_OF_MONTH, start.getDayOfMonth() - 1);
        }

        calendar.set(Calendar.HOUR_OF_DAY, now.getHour());
        calendar.set(Calendar.MINUTE, now.getMinute());
        calendar.set(Calendar.SECOND, now.getSecond());
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.add(Calendar.SECOND, nextSecond);
        return calendar.getTime();
    }

    /**
     * 计算从上次触发时间到当前时间，已触发了多少次了.
     *
     * @param lastTriggerTime 上次触发时间
     * @param now             当前时间
     * @return 返回已触犯的次数，最小值为0
     */
    public int triggerTimes(Date lastTriggerTime, Date now) {
        // 如果刷新时间比当前时间还大，那就直接返回0
        if (lastTriggerTime.getTime() >= now.getTime()) {
            return 0;
        }

        final int lastTriggerSecond = DateUtils.toLocalTime(lastTriggerTime).toSecondOfDay();
        final int nowSecond = DateUtils.toLocalTime(now).toSecondOfDay();
        final long days = DateUtils.diffDays(now, lastTriggerTime);

        int result = 0;

        for (LocalTime time : array) {
            int targetSecond = time.toSecondOfDay();
            // 上次触发时间不是今天
            if (days > 0) {
                // 今天触发的次数
                if (targetSecond <= nowSecond) {
                    result++;
                }
                // 最前面那天的次数
                if (lastTriggerSecond < targetSecond) {
                    result++;
                }
            }
            // 上次触发时间是今天
            else if (lastTriggerSecond < targetSecond && targetSecond <= nowSecond) {
                result++;
            }
        }

        // 中间的天数要算全部
        if (days > 1) {
            result += (days - 1) * array.length;
        }
        return result;
    }

    /**
     * 判定指定时间是不是在这个区间之内.
     *
     * @param localTime 指定时间
     * @return 如果在这个区间内返回true，否则返回false
     */
    public boolean inSection(LocalTime localTime) {
        if (array.length != 2) {
            throw new ConvertException("只有配置两个时间，还可以使用此方法" + this);
        }

        long startTime = array[0].toSecondOfDay();
        long endTime = array[1].toSecondOfDay();
        long now = localTime.toSecondOfDay();
        return startTime <= now && now <= endTime;
    }

    @Override
    public String toString() {
        return "LocalTimeArray [array=" + Arrays.toString(array) + "]";
    }
}