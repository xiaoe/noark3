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
package com.company.game.domain;

import static xyz.noark.log.LogHelper.logger;

import java.time.Instant;
import java.util.Date;
import java.util.concurrent.atomic.LongAdder;

import javax.annotation.PostConstruct;

import com.company.game.LoginEvent;
import com.company.game.event.BuildingUpgradeEvent;

import xyz.noark.core.annotation.Autowired;
import xyz.noark.core.annotation.Repository;
import xyz.noark.core.util.DateUtils;
import xyz.noark.game.event.EventManager;
import xyz.noark.orm.repository.UniqueCacheRepository;

/**
 * 道具实体访问类.
 *
 * @since 3.2
 * @author 小流氓(176543888@qq.com)
 */
@Repository
public class ItemRepository extends UniqueCacheRepository<Item, Integer> {

	@Autowired
	private EventManager eventManager;

	public ItemRepository() {
		System.out.println("............");
	}
	
	@PostConstruct
	public void test() {
		Item item = this.cacheGet(1);
		if (item == null) {
			item = new Item();
			item.setId(1);
			item.setTodayBuy(new LongAdder());
			item.setRead(true);
			item.setCreateTime(new Date());
			item.setModifyTime(item.getCreateTime());
			item.setTestTime(Instant.now());
			this.cacheInsert(item);
		}

		item.getTodayBuy().add(100);
		item.setRead(false);
		this.cacheUpdate(item);

		this.cacheDelete(item);

		eventManager.publish(new LoginEvent());

		// 测试延迟事件
		BuildingUpgradeEvent event = new BuildingUpgradeEvent();
		event.setId(1);
		event.setEndTime(DateUtils.addSeconds(new Date(), 5));
		eventManager.publish(event);
		logger.debug("建筑开始升级了...");
	}
}