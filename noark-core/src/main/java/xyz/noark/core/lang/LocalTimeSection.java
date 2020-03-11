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

/**
 * 时间区间.
 *
 * @since 3.4
 * @author 小流氓(176543888@qq.com)
 */
public class LocalTimeSection {
	/** 是否匹配全部 */
	private final boolean flag;
	private final long startTime;
	private final long endTime;

	public LocalTimeSection() {
		this(true, null, null);
	}

	public LocalTimeSection(LocalTime startTime, LocalTime endTime) {
		this(false, startTime, endTime);
	}

	public LocalTimeSection(boolean flag, LocalTime startTime, LocalTime endTime) {
		this.flag = flag;
		this.startTime = startTime == null ? 0L : startTime.toNanoOfDay();
		this.endTime = endTime == null ? 0L : endTime.toNanoOfDay();
	}

	public boolean isValid(LocalTime localTime) {
		if (flag) {
			return true;
		}

		final long now = localTime.toNanoOfDay();

		// 正向规则：startTime < localTime < endTime
		if (endTime >= startTime) {
			return startTime <= now && now <= endTime;
		}

		// 跨天的情况, 大于开始或小于结束 20:00-08:00
		return now >= startTime || now <= endTime;
	}
}