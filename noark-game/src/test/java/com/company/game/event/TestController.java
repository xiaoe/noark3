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
package com.company.game.event;

import static xyz.noark.log.LogHelper.logger;

import java.util.Arrays;

import xyz.noark.core.annotation.Controller;
import xyz.noark.core.annotation.Order;
import xyz.noark.core.annotation.controller.EventListener;
import xyz.noark.core.annotation.controller.ExecThreadGroup;
import xyz.noark.core.ioc.manager.EventMethodManager;

/**
 * Order排序测试入口...
 *
 * @since 3.3.6
 * @author 小流氓[176543888@qq.com]
 */
@Controller(threadGroup = ExecThreadGroup.ModuleThreadGroup)
public class TestController {
	@Order(-1)
	@EventListener
	public void test1(TestOrderEvent event) {
		logger.debug("test1..................................................");
	}

	@Order(-2)
	@EventListener
	public void test2(TestOrderEvent event) {
		logger.debug("test2..................................................");
	}

	@Order(3)
	@EventListener
	public void test3(TestOrderEvent event) {
		logger.debug("test3..................................................");
	}

	@EventListener
	public void test4(TestOrderEvent event) {
		logger.debug("test4..................................................");
		System.out.println(Arrays.toString(EventMethodManager.getInstance().getEventMethodWrappers(TestOrderEvent.class).toArray()));
		logger.debug("test4..................................................");
	}
}
