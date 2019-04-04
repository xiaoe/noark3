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
package xyz.noark.core.cron;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import xyz.noark.core.util.StringUtils;

/**
 * CRON表达式.
 * <p>
 * 这个类来源于Spring4.3，待优化...
 *
 * @author 小流氓(176543888@qq.com)
 */
public class CronExpression {
	private final String expression;
	private final TimeZone timeZone;
	private final BitSet months = new BitSet(12);
	private final BitSet daysOfMonth = new BitSet(31);
	private final BitSet daysOfWeek = new BitSet(7);
	private final BitSet hours = new BitSet(24);
	private final BitSet minutes = new BitSet(60);
	private final BitSet seconds = new BitSet(60);

	public CronExpression(String expression) {
		this(expression, TimeZone.getDefault());
	}

	public CronExpression(String expression, TimeZone timeZone) {
		this.expression = expression;
		this.timeZone = timeZone;
		parse(expression);
	}

	private CronExpression(String expression, String[] fields) {
		this.expression = expression;
		this.timeZone = null;
		doParse(fields);
	}

	public Date next(Date date) {
		Calendar calendar = new GregorianCalendar();
		calendar.setTimeZone(this.timeZone);
		calendar.setTime(date);
		calendar.set(Calendar.MILLISECOND, 0);

		long originalTimestamp = calendar.getTimeInMillis();
		doNext(calendar, calendar.get(Calendar.YEAR));

		if (calendar.getTimeInMillis() == originalTimestamp) {
			calendar.add(Calendar.SECOND, 1);
			doNext(calendar, calendar.get(Calendar.YEAR));
		}

		return calendar.getTime();
	}

	private void doNext(Calendar calendar, int dot) {
		List<Integer> resets = new ArrayList<Integer>();

		int second = calendar.get(Calendar.SECOND);
		List<Integer> emptyList = Collections.emptyList();
		int updateSecond = findNext(this.seconds, second, calendar, Calendar.SECOND, Calendar.MINUTE, emptyList);
		if (second == updateSecond) {
			resets.add(Calendar.SECOND);
		}

		int minute = calendar.get(Calendar.MINUTE);
		int updateMinute = findNext(this.minutes, minute, calendar, Calendar.MINUTE, Calendar.HOUR_OF_DAY, resets);
		if (minute == updateMinute) {
			resets.add(Calendar.MINUTE);
		} else {
			doNext(calendar, dot);
		}

		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int updateHour = findNext(this.hours, hour, calendar, Calendar.HOUR_OF_DAY, Calendar.DAY_OF_WEEK, resets);
		if (hour == updateHour) {
			resets.add(Calendar.HOUR_OF_DAY);
		} else {
			doNext(calendar, dot);
		}

		int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
		int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
		int updateDayOfMonth = findNextDay(calendar, this.daysOfMonth, dayOfMonth, daysOfWeek, dayOfWeek, resets);
		if (dayOfMonth == updateDayOfMonth) {
			resets.add(Calendar.DAY_OF_MONTH);
		} else {
			doNext(calendar, dot);
		}

		int month = calendar.get(Calendar.MONTH);
		int updateMonth = findNext(this.months, month, calendar, Calendar.MONTH, Calendar.YEAR, resets);
		if (month != updateMonth) {
			if (calendar.get(Calendar.YEAR) - dot > 4) {
				throw new IllegalArgumentException("Invalid cron expression \"" + this.expression + "\" led to runaway search for next trigger");
			}
			doNext(calendar, dot);
		}
	}

	private int findNextDay(Calendar calendar, BitSet daysOfMonth, int dayOfMonth, BitSet daysOfWeek, int dayOfWeek, List<Integer> resets) {
		int count = 0;
		int max = 366;
		while ((!daysOfMonth.get(dayOfMonth) || !daysOfWeek.get(dayOfWeek - 1)) && count++ < max) {
			calendar.add(Calendar.DAY_OF_MONTH, 1);
			dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
			dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
			reset(calendar, resets);
		}
		if (count >= max) {
			throw new IllegalArgumentException("Overflow in day for expression \"" + this.expression + "\"");
		}
		return dayOfMonth;
	}

	private int findNext(BitSet bits, int value, Calendar calendar, int field, int nextField, List<Integer> lowerOrders) {
		int nextValue = bits.nextSetBit(value);
		if (nextValue == -1) {
			calendar.add(nextField, 1);
			reset(calendar, Collections.singletonList(field));
			nextValue = bits.nextSetBit(0);
		}
		if (nextValue != value) {
			calendar.set(field, nextValue);
			reset(calendar, lowerOrders);
		}
		return nextValue;
	}

	private void reset(Calendar calendar, List<Integer> fields) {
		for (int field : fields) {
			calendar.set(field, field == Calendar.DAY_OF_MONTH ? 1 : 0);
		}
	}

	private void parse(String expression) throws IllegalArgumentException {
		String[] fields = StringUtils.split(expression, " ");
		if (!areValidCronFields(fields)) {
			throw new IllegalArgumentException(String.format("Cron expression must consist of 6 fields (found %d in \"%s\")", fields.length, expression));
		}
		doParse(fields);
	}

	private void doParse(String[] fields) {
		setNumberHits(this.seconds, fields[0], 0, 60);
		setNumberHits(this.minutes, fields[1], 0, 60);
		setNumberHits(this.hours, fields[2], 0, 24);
		setDaysOfMonth(this.daysOfMonth, fields[3]);
		setMonths(this.months, fields[4]);
		setDays(this.daysOfWeek, replaceOrdinals(fields[5], "SUN,MON,TUE,WED,THU,FRI,SAT"), 8);
		if (this.daysOfWeek.get(7)) {
			this.daysOfWeek.set(0);
			this.daysOfWeek.clear(7);
		}
	}

	private String replaceOrdinals(String value, String commaSeparatedList) {
		String[] list = StringUtils.split(commaSeparatedList, ",");
		for (int i = 0; i < list.length; i++) {
			String item = list[i].toUpperCase();
			value = value.toUpperCase().replace(item, "" + i);
		}
		return value;
	}

	private void setDaysOfMonth(BitSet bits, String field) {
		int max = 31;
		setDays(bits, field, max + 1);
		bits.clear(0);
	}

	private void setDays(BitSet bits, String field, int max) {
		if (field.contains("?")) {
			field = "*";
		}
		setNumberHits(bits, field, 0, max);
	}

	private void setMonths(BitSet bits, String value) {
		int max = 12;
		value = replaceOrdinals(value, "FOO,JAN,FEB,MAR,APR,MAY,JUN,JUL,AUG,SEP,OCT,NOV,DEC");
		BitSet months = new BitSet(13);
		setNumberHits(months, value, 1, max + 1);
		for (int i = 1; i <= max; i++) {
			if (months.get(i)) {
				bits.set(i - 1);
			}
		}
	}

	private void setNumberHits(BitSet bits, String value, int min, int max) {
		String[] fields = StringUtils.split(value, ",");
		for (String field : fields) {
			if (!field.contains("/")) {
				int[] range = getRange(field, min, max);
				bits.set(range[0], range[1] + 1);
			} else {
				String[] split = StringUtils.split(field, "/");
				if (split.length > 2) {
					throw new IllegalArgumentException("Incrementer has more than two fields: '" + field + "' in expression \"" + this.expression + "\"");
				}
				int[] range = getRange(split[0], min, max);
				if (!split[0].contains("-")) {
					range[1] = max - 1;
				}
				int delta = Integer.parseInt(split[1]);
				if (delta <= 0) {
					throw new IllegalArgumentException("Incrementer delta must be 1 or higher: '" + field + "' in expression \"" + this.expression + "\"");
				}
				for (int i = range[0]; i <= range[1]; i += delta) {
					bits.set(i);
				}
			}
		}
	}

	private int[] getRange(String field, int min, int max) {
		int[] result = new int[2];
		if (field.contains("*")) {
			result[0] = min;
			result[1] = max - 1;
			return result;
		}
		if (!field.contains("-")) {
			result[0] = result[1] = Integer.valueOf(field);
		} else {
			String[] split = StringUtils.split(field, "-");
			if (split.length > 2) {
				throw new IllegalArgumentException("Range has more than two fields: '" + field + "' in expression \"" + this.expression + "\"");
			}
			result[0] = Integer.valueOf(split[0]);
			result[1] = Integer.valueOf(split[1]);
		}
		if (result[0] >= max || result[1] >= max) {
			throw new IllegalArgumentException("Range exceeds maximum (" + max + "): '" + field + "' in expression \"" + this.expression + "\"");
		}
		if (result[0] < min || result[1] < min) {
			throw new IllegalArgumentException("Range less than minimum (" + min + "): '" + field + "' in expression \"" + this.expression + "\"");
		}
		if (result[0] > result[1]) {
			throw new IllegalArgumentException("Invalid inverted range: '" + field + "' in expression \"" + this.expression + "\"");
		}
		return result;
	}

	public static boolean isValidExpression(String expression) {
		if (expression == null) {
			return false;
		}
		String[] fields = StringUtils.split(expression, " ");
		if (!areValidCronFields(fields)) {
			return false;
		}
		try {
			new CronExpression(expression, fields);
			return true;
		} catch (IllegalArgumentException ex) {
			return false;
		}
	}

	private static boolean areValidCronFields(String[] fields) {
		return (fields != null && fields.length == 6);
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof CronExpression)) {
			return false;
		}
		CronExpression otherCron = (CronExpression) other;
		return (this.months.equals(otherCron.months) && this.daysOfMonth.equals(otherCron.daysOfMonth) && this.daysOfWeek.equals(otherCron.daysOfWeek) && this.hours.equals(otherCron.hours) && this.minutes.equals(otherCron.minutes)
				&& this.seconds.equals(otherCron.seconds));
	}

	@Override
	public int hashCode() {
		return (17 * this.months.hashCode() + 29 * this.daysOfMonth.hashCode() + 37 * this.daysOfWeek.hashCode() + 41 * this.hours.hashCode() + 53 * this.minutes.hashCode() + 61 * this.seconds.hashCode());
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + ": " + this.expression;
	}
}