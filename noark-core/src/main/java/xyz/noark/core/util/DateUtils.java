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

import java.time.*;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * 时间相关操作工具类.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.0
 */
public class DateUtils {
    /**
     * 每秒有1000毫秒
     */
    public static final int MILLISECOND_PER_SECOND = 1000;
    /**
     * 每分钟有60秒
     */
    public static final int SECOND_PER_MINUTE = 60;
    /**
     * 每小时有60分钟
     */
    public static final int MINUTE_PER_HOUR = 60;
    /**
     * 每天有24小时
     */
    public static final int HOUR_PER_DAY = 24;

    /**
     * 判断两个日期时间是否是同一天 。
     *
     * @param date1 第一个日期
     * @param date2 第二个日期
     * @return 如果是同一天返回true, 否则返回false
     */
    public static boolean isSameDay(final Date date1, final Date date2) {
        return isSameDay(date1, date2, 0);
    }

    /**
     * 判断两个日期时间是否是同一天，带秒钟偏移值
     * <p>
     * 例如：需要在次日的5点刷新，offset传值为 5*3600 = 18000
     * </p>
     *
     * @param date1  第一个日期
     * @param date2  第二个日期
     * @param offset 秒钟偏移值
     * @return 如果是同一天返回true, 否则返回false
     */
    public static boolean isSameDay(final Date date1, final Date date2, final int offset) {
        final Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        cal1.add(Calendar.SECOND, -offset);

        final Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        cal2.add(Calendar.SECOND, -offset);

        return isSameDay(cal1, cal2);
    }

    /**
     * <p>
     * Checks if two calendar objects are on the same day ignoring time.
     * </p>
     *
     * <p>
     * 28 Mar 2002 13:45 and 28 Mar 2002 06:01 would return true. 28 Mar 2002 13:45 and 12 Mar 2002 13:45 would return false.
     * </p>
     *
     * @param cal1 the first calendar, not altered, not null
     * @param cal2 the second calendar, not altered, not null
     * @return true if they represent the same day
     * @throws IllegalArgumentException if either calendar is <code>null</code>
     */
    public static boolean isSameDay(final Calendar cal1, final Calendar cal2) {
        return cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA) && cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) && cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }

    /**
     * 判断两个日期时间是否是同周.
     * <p>
     * WeekFields.ISO代表每周从周一开始算。(默认)<br>
     * WeekFields.SUNDAY_START代表每周从周日开始算。
     * </p>
     *
     * @param date1 第一个日期
     * @param date2 第二个日期
     * @return 如果是同周返回true, 否则返回false
     */
    public static boolean isSameWeek(final LocalDate date1, final LocalDate date2) {
        return isSameWeek(date1, date2, WeekFields.ISO);
    }

    /**
     * 判断两个日期时间是否是同周.
     * <p>
     * WeekFields.ISO代表每周从周一开始算。(建议)<br>
     * WeekFields.SUNDAY_START代表每周从周日开始算。
     * </p>
     *
     * @param date1      第一个日期
     * @param date2      第二个日期
     * @param weekFields 星期配置（建议使用ISO规范）
     * @return 如果是同周返回true, 否则返回false
     */
    public static boolean isSameWeek(final LocalDate date1, final LocalDate date2, WeekFields weekFields) {
        final TemporalField temporalField = weekFields.weekOfWeekBasedYear();
        return date1.get(temporalField) == date2.get(temporalField);
    }

    /**
     * 判断两个日期时间是否是同周。
     * <p>
     * WeekFields.ISO代表每周从周一开始算。(默认)<br>
     * WeekFields.SUNDAY_START代表每周从周日开始算。
     * </p>
     *
     * @param date1 第一个日期
     * @param date2 第二个日期
     * @return 如果是同周返回true, 否则返回false
     */
    public static boolean isSameWeek(final Date date1, final Date date2) {
        return isSameWeek(DateUtils.toLocalDate(date1), DateUtils.toLocalDate(date2));
    }

    /**
     * 判断两个日期时间是否是同周
     * <p>
     * WeekFields.ISO代表每周从周一开始算。(建议)<br>
     * WeekFields.SUNDAY_START代表每周从周日开始算。
     * </p>
     *
     * @param date1      第一个日期
     * @param date2      第二个日期
     * @param weekFields 星期配置（建议使用ISO规范）
     * @return 如果是同周返回true, 否则返回false
     */
    public static boolean isSameWeek(final Date date1, final Date date2, WeekFields weekFields) {
        return isSameWeek(DateUtils.toLocalDate(date1), DateUtils.toLocalDate(date2), weekFields);
    }

    /**
     * 判断两个日期时间是否是同年同月份
     *
     * @param date1 第一个日期
     * @param date2 第二个日期
     * @return 如果是同一个月份返回true, 否则返回false
     */
    public static boolean isSameMonth(final Date date1, final Date date2) {
        final Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        final Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        return isSameMonth(cal1, cal2);
    }

    /**
     * 判断两个日期时间是否是同一周，带秒钟偏移值
     * <p>
     * WeekFields.ISO代表每周从周一开始算。(建议)<br>
     * WeekFields.SUNDAY_START代表每周从周日开始算。
     * 例如：需要在次周的5点刷新，offset传值为 5*3600 = 18000
     * </p>
     *
     * @param date1  第一个日期
     * @param date2  第二个日期
     * @param offset 秒钟偏移值
     * @return 如果是同一周返回true, 否则返回false
     */
    public static boolean isSameWeek(final Date date1, final Date date2, final int offset) {
        final Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        cal1.add(Calendar.SECOND, -offset);

        final Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        cal2.add(Calendar.SECOND, -offset);

        return isSameWeek(DateUtils.toLocalDate(cal1.getTime()), DateUtils.toLocalDate(cal2.getTime()));
    }

    /**
     * 判定两个日期是否为同年同月份
     *
     * @param cal1 第一个日期
     * @param cal2 第二个日期
     * @return 如果是同一个月份返回true, 否则返回false
     */
    public static boolean isSameMonth(Calendar cal1, Calendar cal2) {
        return cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA) && cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) && cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH);
    }

    /**
     * Adds a number of years to a date returning a new object. The original {@code Date} is unchanged.
     *
     * @param date   the date, not null
     * @param amount the amount to add, may be negative
     * @return the new {@code Date} with the amount added
     * @throws IllegalArgumentException if the date is null
     */
    public static Date addYears(final Date date, final int amount) {
        return add(date, Calendar.YEAR, amount);
    }

    /**
     * Adds a number of months to a date returning a new object. The original {@code Date} is unchanged.
     *
     * @param date   the date, not null
     * @param amount the amount to add, may be negative
     * @return the new {@code Date} with the amount added
     * @throws IllegalArgumentException if the date is null
     */
    public static Date addMonths(final Date date, final int amount) {
        return add(date, Calendar.MONTH, amount);
    }

    // -----------------------------------------------------------------------

    /**
     * Adds a number of weeks to a date returning a new object. The original {@code Date} is unchanged.
     *
     * @param date   the date, not null
     * @param amount the amount to add, may be negative
     * @return the new {@code Date} with the amount added
     * @throws IllegalArgumentException if the date is null
     */
    public static Date addWeeks(final Date date, final int amount) {
        return add(date, Calendar.WEEK_OF_YEAR, amount);
    }

    // -----------------------------------------------------------------------

    /**
     * Adds a number of days to a date returning a new object. The original {@code Date} is unchanged.
     *
     * @param date   the date, not null
     * @param amount the amount to add, may be negative
     * @return the new {@code Date} with the amount added
     * @throws IllegalArgumentException if the date is null
     */
    public static Date addDays(final Date date, final int amount) {
        return add(date, Calendar.DAY_OF_MONTH, amount);
    }

    /**
     * Adds a number of hours to a date returning a new object. The original {@code Date} is unchanged.
     *
     * @param date   the date, not null
     * @param amount the amount to add, may be negative
     * @return the new {@code Date} with the amount added
     * @throws IllegalArgumentException if the date is null
     */
    public static Date addHours(final Date date, final int amount) {
        return add(date, Calendar.HOUR_OF_DAY, amount);
    }

    /**
     * Adds a number of minutes to a date returning a new object. The original {@code Date} is unchanged.
     *
     * @param date   the date, not null
     * @param amount the amount to add, may be negative
     * @return the new {@code Date} with the amount added
     * @throws IllegalArgumentException if the date is null
     */
    public static Date addMinutes(final Date date, final int amount) {
        return add(date, Calendar.MINUTE, amount);
    }

    /**
     * Adds a number of seconds to a date returning a new object. The original {@code Date} is unchanged.
     *
     * @param date   the date, not null
     * @param amount the amount to add, may be negative
     * @return the new {@code Date} with the amount added
     * @throws IllegalArgumentException if the date is null
     */
    public static Date addSeconds(final Date date, final int amount) {
        return add(date, Calendar.SECOND, amount);
    }

    /**
     * Adds a number of milliseconds to a date returning a new object. The original {@code Date} is unchanged.
     *
     * @param date   the date, not null
     * @param amount the amount to add, may be negative
     * @return the new {@code Date} with the amount added
     * @throws IllegalArgumentException if the date is null
     */
    public static Date addMilliseconds(final Date date, final int amount) {
        return add(date, Calendar.MILLISECOND, amount);
    }

    /**
     * Adds to a date returning a new object. The original {@code Date} is unchanged.
     *
     * @param date          the date, not null
     * @param calendarField the calendar field to add to
     * @param amount        the amount to add, may be negative
     * @return the new {@code Date} with the amount added
     * @throws IllegalArgumentException if the date is null
     */
    private static Date add(final Date date, final int calendarField, final int amount) {
        // 如果加上0的话，那就是不变，不要再计算啦
        if (amount == 0) {
            return date;
        }

        final Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(calendarField, amount);
        return c.getTime();
    }

    /**
     * 将Date对象转化为秒数.
     * <p>
     * 为了代码里不要到处出现<code>{@link Date#getTime()} / 1000</code>的情况<br>
     *
     * @param date Date日期
     * @return 返回这个日期所对应的秒数
     */
    public static long toSeconds(Date date) {
        return toSeconds(date.getTime());
    }

    /**
     * 将毫秒数转化为秒数.
     * <p>
     * 为了代码里不要到处出现<code>{@link Date#getTime()} / 1000</code>的情况<br>
     *
     * @param milliseconds 毫秒数
     * @return 返回这个毫秒数所对应的秒数
     */
    public static long toSeconds(long milliseconds) {
        return milliseconds / MILLISECOND_PER_SECOND;
    }

    /**
     * 获取指定日期那天的开始时间并转化为秒数
     *
     * @param localDate 指定日期
     * @return 返回指定日期那天的开始时间并转化为秒数
     */
    public static long toSecondsByStartOfDay(LocalDate localDate) {
        return localDate.atStartOfDay(ZoneOffset.systemDefault()).toEpochSecond();
    }

    /**
     * 将Date对象转化为天数.
     *
     * @param date Date日期
     * @return 返回这个日期所对应的天数
     */
    public static long toDays(Date date) {
        return toDays(date.getTime() + TimeZone.getDefault().getRawOffset());
    }

    /**
     * 将毫秒数转化为天数.
     *
     * @param milliseconds 毫秒数
     * @return 返回这个毫秒数所对应的天数
     */
    public static long toDays(long milliseconds) {
        return milliseconds / (1L * MILLISECOND_PER_SECOND * SECOND_PER_MINUTE * MINUTE_PER_HOUR * HOUR_PER_DAY);
    }

    /**
     * Date对象转化为LocalDateTime对象
     *
     * @param date Date对象
     * @return LocalDateTime对象
     */
    public static LocalDateTime toLocalDateTime(Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

    /**
     * Date对象转化为LocalDate对象。
     * <p>
     * 时间会丢掉了哈，要注意，转过去就回不来了
     *
     * @param date Date对象
     * @return LocalDate对象
     */
    public static LocalDate toLocalDate(Date date) {
        return toLocalDateTime(date).toLocalDate();
    }

    /**
     * Date对象转化为LocalTime对象
     *
     * @param date Date对象
     * @return LocalTime对象
     */
    public static LocalTime toLocalTime(Date date) {
        return toLocalDateTime(date).toLocalTime();
    }

    /**
     * LocalDateTime对象转回Date对象.
     *
     * @param localDateTime LocalDateTime对象
     * @return Date对象
     */
    public static Date from(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * 计算两个Date对象之间相差多少秒.
     *
     * @param date1 时间一
     * @param date2 时间二
     * @return 秒数差，如果时间2大于时间1，有可能会是负值噢.
     */
    public static long diffSeconds(Date date1, Date date2) {
        // 只是计算两个时间秒差，不需要对时区处理，要多一起多,要少一起少
        return toSeconds(date1) - toSeconds(date2);
    }

    /**
     * 计算两个Date对象之间相差多少天.
     * <p>
     * 由于使用毫秒计算的方式，所以计算天数需要处理时区问题...
     *
     * @param date1 时间一
     * @param date2 时间二
     * @return 天数差，如果时间2大于时间1，有可能会是负值噢.
     */
    public static long diffDays(Date date1, Date date2) {
        final int offset = TimeZone.getDefault().getRawOffset();
        return toDays(date1.getTime() + offset) - toDays(date2.getTime() + offset);
    }

    /**
     * 将一个秒数格式化为一个时间格式 HH:mm:ss
     * <p>
     * 游戏中规则显示，用于格式参数等格式秒数
     *
     * @param seconds 一个毫秒数
     * @return 时间格式 HH:mm:ss
     */
    public static String formatTime(long seconds) {
        if (seconds <= 0) {
            return "00:00:00";
        }
        // 秒数
        int second = (int) (seconds % SECOND_PER_MINUTE);
        seconds = seconds / SECOND_PER_MINUTE;
        // 分钟
        int minute = (int) (seconds % MINUTE_PER_HOUR);
        seconds = seconds / MINUTE_PER_HOUR;
        // 最后剩的都是小时
        long hour = seconds;

        StringBuilder sb = new StringBuilder(StringUtils.asciiSizeInBytes(hour) + 6);
        sb.append(hour < 10 ? "0" : "").append(hour).append(":");
        sb.append(minute < 10 ? "0" : "").append(minute).append(":");
        sb.append(second < 10 ? "0" : "").append(second);
        return sb.toString();
    }

    /**
     * 把纳秒转化为毫秒显示（保留小数点后面两位）
     *
     * @param nanoTime 纳秒
     * @return 毫秒
     */
    public static float formatNanoTime(long nanoTime) {
        // 除100W，然后格式化
        return MathUtils.formatScale(nanoTime / 100_0000F, 2);
    }

    /**
     * 获取指定日期的星期几属性，返回枚举结果{@code DayOfWeek}
     *
     * @param date 指定日期
     * @return 每周中的第几天，不为空
     */
    public static DayOfWeek getDayOfWeek(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).getDayOfWeek();
    }

    /**
     * 获取指定时间那天的开始时间
     *
     * @param date 指定时间
     * @return 那天的开始时间
     */
    public static Date getStartOfDay(Date date) {
        Calendar now = Calendar.getInstance();
        now.setTime(date);
        now.set(Calendar.HOUR_OF_DAY, 0);
        now.set(Calendar.MINUTE, 0);
        now.set(Calendar.SECOND, 0);
        now.set(Calendar.MILLISECOND, 0);
        return now.getTime();
    }
}