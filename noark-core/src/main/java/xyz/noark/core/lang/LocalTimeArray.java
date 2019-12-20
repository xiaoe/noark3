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
package xyz.noark.core.lang;

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
 * @since 3.3.9
 * @author 小流氓(176543888@qq.com)
 */
public class LocalTimeArray {
	/** 每天最大的秒数 */
	private static final int MAX_SECOND_BY_DAY = 24 * 60 * 60 - 1;

	private final LocalTime[] array;

	public LocalTimeArray(LocalTime[] array) {
		this.array = array;
	}

	public LocalTime[] getArray() {
		return array;
	}

	public Date doNext() {
		return doNext(LocalTime.now());
	}

	public Date doNext(LocalTime now) {
		// 计算出来最小的那个时间
		int minSecond = MAX_SECOND_BY_DAY;
		int nextSecond = MAX_SECOND_BY_DAY;
		final int todaySecond = now.toSecondOfDay();
		for (LocalTime time : array) {
			int targetSecond = time.toSecondOfDay();
			minSecond = Math.min(minSecond, targetSecond);
			if (targetSecond > todaySecond) {
				nextSecond = Math.min(nextSecond, targetSecond - todaySecond);
			}
		}

		// 还是初始值，那就计算过天的下个时间
		if (nextSecond == MAX_SECOND_BY_DAY) {
			nextSecond = MAX_SECOND_BY_DAY + 1 - todaySecond + minSecond;
		}

		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, now.getHour());
		calendar.set(Calendar.MINUTE, now.getMinute());
		calendar.set(Calendar.SECOND, now.getSecond());
		calendar.add(Calendar.SECOND, nextSecond);
		return calendar.getTime();
	}

	@Override
	public String toString() {
		return "LocalTimeArray [array=" + Arrays.toString(array) + "]";
	}
}