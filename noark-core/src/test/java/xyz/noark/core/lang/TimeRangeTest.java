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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.LocalDateTime;

import org.junit.Test;

/**
 * 时间范围表达式测试.
 *
 * @since 3.4
 * @author 小流氓[176543888@qq.com]
 */
public class TimeRangeTest {

	@Test
	public void testIsValid() {
		TimeRange range = new TimeRange("[2020][5-6][11,12,15-19][w1,w3-w5][12:00-13:00]");
		assertFalse(range.isValid(LocalDateTime.of(2019, 5, 11, 12, 00)));
		assertFalse(range.isValid(LocalDateTime.of(2020, 4, 11, 12, 00)));
		assertFalse(range.isValid(LocalDateTime.of(2020, 5, 14, 12, 00)));
		assertFalse(range.isValid(LocalDateTime.of(2020, 5, 16, 12, 00)));
		assertTrue(range.isValid(LocalDateTime.of(2020, 5, 11, 12, 00)));
	}

}
