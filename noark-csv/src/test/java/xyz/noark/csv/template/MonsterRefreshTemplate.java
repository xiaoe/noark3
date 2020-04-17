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
package xyz.noark.csv.template;

import xyz.noark.core.annotation.tpl.TplAttr;
import xyz.noark.core.annotation.tpl.TplFile;
import xyz.noark.core.lang.IntList;

/**
 * 怪物刷新模板配置.
 *
 * @author 小流氓[176543888@qq.com]
 */
@TplFile("MonsterRefresh.tpl")
public class MonsterRefreshTemplate {
	/** 郡城等级 */
	@TplAttr(name = "Level")
	private int level;
	/** 服务器时长（小时） */
	@TplAttr(name = "ServerPeriod")
	private int serverPeriod;
	/** 刷新半径(里) */
	@TplAttr(name = "Range")
	private int range;
	/** 刷新时间(s) */
	@TplAttr(name = "RefreshTime")
	private int refreshTime;

	/** 刷新怪物类型 */
	@TplAttr(name = "MonsterType")
	private int monsterType;

	/** 1级怪数量 */
	@TplAttr(name = "Level1")
	@TplAttr(name = "Level2")
	@TplAttr(name = "Level3")
	@TplAttr(name = "Level4")
	@TplAttr(name = "Level5")
	@TplAttr(name = "Level6")
	@TplAttr(name = "Level7")
	@TplAttr(name = "Level8")
	@TplAttr(name = "Level9")
	@TplAttr(name = "Level10")
	@TplAttr(name = "Level11")
	@TplAttr(name = "Level12")
	@TplAttr(name = "Level13")
	@TplAttr(name = "Level14")
	@TplAttr(name = "Level15")
	@TplAttr(name = "Level16")
	@TplAttr(name = "Level17")
	@TplAttr(name = "Level18")
	@TplAttr(name = "Level19")
	@TplAttr(name = "Level20")
	@TplAttr(name = "Level21")
	@TplAttr(name = "Level22")
	@TplAttr(name = "Level23")
	@TplAttr(name = "Level24")
	@TplAttr(name = "Level25")
	@TplAttr(name = "Level26")
	@TplAttr(name = "Level27")
	@TplAttr(name = "Level28")
	@TplAttr(name = "Level29")
	@TplAttr(name = "Level30")
	@TplAttr(name = "Level31")
	@TplAttr(name = "Level32")
	@TplAttr(name = "Level33")
	@TplAttr(name = "Level34")
	@TplAttr(name = "Level35")
	private IntList levelNumList;

	public int getLevel() {
		return level;
	}

	public int getMonsterType() {
		return monsterType;
	}

	public int getServerPeriod() {
		return serverPeriod;
	}

	public int getRange() {
		return range;
	}

	public int getRefreshTime() {
		return refreshTime;
	}

	public IntList getLevelNumList() {
		return levelNumList;
	}
}