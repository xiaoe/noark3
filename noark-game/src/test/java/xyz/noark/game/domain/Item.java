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
package xyz.noark.game.domain;

import java.util.Date;
import java.util.concurrent.atomic.LongAdder;

import xyz.noark.core.annotation.orm.Column;
import xyz.noark.core.annotation.orm.Entity;
import xyz.noark.core.annotation.orm.Entity.FeatchType;
import xyz.noark.core.annotation.orm.Id;
import xyz.noark.core.annotation.orm.Table;

/**
 * 道具表.
 *
 * @since 3.2
 * @author 小流氓(176543888@qq.com)
 */
@Entity(fetch = FeatchType.START)
@Table(name = "item")
public class Item {

	@Id
	@Column(name = "id")
	private int id;

	@Column(name = "template_id", length = 17)
	private int templateId;

	@Column(name = "attr", length = 128, defaultValue = "{}")
	private String attr;

	@Column(name = "read")
	private boolean read;

	@Column(name = "today_buy", defaultValue = "1", nullable = false, comment = "今天购买次数")
	private LongAdder todayBuy;

	@Column(name = "create_time", nullable = false, comment = "创建时间", defaultValue = "2018-07-06 05:04:03")
	private Date createTime;

	@Column(name = "modify_time", nullable = false, comment = "修改时间", defaultValue = "2018-07-06 05:04:03")
	private Date modifyTime;

	public int getId() {
		return id;
	}

	public int getTemplateId() {
		return templateId;
	}

	public void setTemplateId(int templateId) {
		this.templateId = templateId;
	}

	public void setId(int id) {
		this.id = id;
	}

	public boolean isRead() {
		return read;
	}

	public String getAttr() {
		return attr;
	}

	public LongAdder getTodayBuy() {
		return todayBuy;
	}

	public void setTodayBuy(LongAdder todayBuy) {
		this.todayBuy = todayBuy;
	}

	public void setAttr(String attr) {
		this.attr = attr;
	}

	public void setRead(boolean read) {
		this.read = read;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getModifyTime() {
		return modifyTime;
	}

	public void setModifyTime(Date modifyTime) {
		this.modifyTime = modifyTime;
	}
}