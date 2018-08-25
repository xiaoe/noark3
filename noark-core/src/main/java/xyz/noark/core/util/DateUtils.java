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

import java.util.Calendar;
import java.util.Date;

/**
 * 时间相关操作工具类.
 *
 * @since 3.0
 * @author 小流氓(176543888@qq.com)
 */
public class DateUtils {

	/**
	 * 判断两个日期时间是否是同一天 。
	 *
	 * @param date1 第一个日期
	 * @param date2 第二个日期
	 * @return 如果是同一天返回true,否则返回false
	 */
	public static boolean isSameDay(final Date date1, final Date date2) {
		final Calendar cal1 = Calendar.getInstance();
		cal1.setTime(date1);
		final Calendar cal2 = Calendar.getInstance();
		cal2.setTime(date2);
		return isSameDay(cal1, cal2);
	}

	/**
	 * <p>
	 * Checks if two calendar objects are on the same day ignoring time.
	 * </p>
	 *
	 * <p>
	 * 28 Mar 2002 13:45 and 28 Mar 2002 06:01 would return true. 28 Mar 2002
	 * 13:45 and 12 Mar 2002 13:45 would return false.
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
	 * Adds a number of years to a date returning a new object. The original
	 * {@code Date} is unchanged.
	 *
	 * @param date the date, not null
	 * @param amount the amount to add, may be negative
	 * @return the new {@code Date} with the amount added
	 * @throws IllegalArgumentException if the date is null
	 */
	public static Date addYears(final Date date, final int amount) {
		return add(date, Calendar.YEAR, amount);
	}

	/**
	 * Adds a number of months to a date returning a new object. The original
	 * {@code Date} is unchanged.
	 *
	 * @param date the date, not null
	 * @param amount the amount to add, may be negative
	 * @return the new {@code Date} with the amount added
	 * @throws IllegalArgumentException if the date is null
	 */
	public static Date addMonths(final Date date, final int amount) {
		return add(date, Calendar.MONTH, amount);
	}

	// -----------------------------------------------------------------------
	/**
	 * Adds a number of weeks to a date returning a new object. The original
	 * {@code Date} is unchanged.
	 *
	 * @param date the date, not null
	 * @param amount the amount to add, may be negative
	 * @return the new {@code Date} with the amount added
	 * @throws IllegalArgumentException if the date is null
	 */
	public static Date addWeeks(final Date date, final int amount) {
		return add(date, Calendar.WEEK_OF_YEAR, amount);
	}

	// -----------------------------------------------------------------------
	/**
	 * Adds a number of days to a date returning a new object. The original
	 * {@code Date} is unchanged.
	 *
	 * @param date the date, not null
	 * @param amount the amount to add, may be negative
	 * @return the new {@code Date} with the amount added
	 * @throws IllegalArgumentException if the date is null
	 */
	public static Date addDays(final Date date, final int amount) {
		return add(date, Calendar.DAY_OF_MONTH, amount);
	}

	/**
	 * Adds a number of hours to a date returning a new object. The original
	 * {@code Date} is unchanged.
	 *
	 * @param date the date, not null
	 * @param amount the amount to add, may be negative
	 * @return the new {@code Date} with the amount added
	 * @throws IllegalArgumentException if the date is null
	 */
	public static Date addHours(final Date date, final int amount) {
		return add(date, Calendar.HOUR_OF_DAY, amount);
	}

	/**
	 * Adds a number of minutes to a date returning a new object. The original
	 * {@code Date} is unchanged.
	 *
	 * @param date the date, not null
	 * @param amount the amount to add, may be negative
	 * @return the new {@code Date} with the amount added
	 * @throws IllegalArgumentException if the date is null
	 */
	public static Date addMinutes(final Date date, final int amount) {
		return add(date, Calendar.MINUTE, amount);
	}

	/**
	 * Adds a number of seconds to a date returning a new object. The original
	 * {@code Date} is unchanged.
	 *
	 * @param date the date, not null
	 * @param amount the amount to add, may be negative
	 * @return the new {@code Date} with the amount added
	 * @throws IllegalArgumentException if the date is null
	 */
	public static Date addSeconds(final Date date, final int amount) {
		return add(date, Calendar.SECOND, amount);
	}

	/**
	 * Adds a number of milliseconds to a date returning a new object. The
	 * original {@code Date} is unchanged.
	 *
	 * @param date the date, not null
	 * @param amount the amount to add, may be negative
	 * @return the new {@code Date} with the amount added
	 * @throws IllegalArgumentException if the date is null
	 */
	public static Date addMilliseconds(final Date date, final int amount) {
		return add(date, Calendar.MILLISECOND, amount);
	}

	/**
	 * Adds to a date returning a new object. The original {@code Date} is
	 * unchanged.
	 *
	 * @param date the date, not null
	 * @param calendarField the calendar field to add to
	 * @param amount the amount to add, may be negative
	 * @return the new {@code Date} with the amount added
	 * @throws IllegalArgumentException if the date is null
	 */
	private static Date add(final Date date, final int calendarField, final int amount) {
		final Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(calendarField, amount);
		return c.getTime();
	}
}
