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
package com.company.test.module.bag.domain;

import java.util.Date;

import xyz.noark.core.annotation.PlayerId;
import xyz.noark.core.annotation.orm.Column;
import xyz.noark.core.annotation.orm.Entity;
import xyz.noark.core.annotation.orm.Id;
import xyz.noark.core.annotation.orm.Json;
import xyz.noark.core.annotation.orm.Table;

/**
 * 道具表.
 *
 * @since 3.0
 * @author 小流氓(176543888@qq.com)
 */
@Entity
@Table(name = "player_item", comment = "道具表")
public class PlayerItem {

	@Id
	@Column(name = "id", comment = "道具ID")
	private long id;

	@PlayerId
	@Column(name = "player_id", nullable = false, comment = "玩家ID")
	private long playerId;

	@Column(name = "name", nullable = false, comment = "道具名称")
	private String name;

	@Column(name = "count", nullable = false, comment = "道具数量", defaultValue = "0")
	private int count;

	@Column(name = "can_use", nullable = false, comment = "能否使用", defaultValue = "false")
	private boolean canUse;

	@Column(name = "price", nullable = false, comment = "价格", defaultValue = "0")
	private float price;

	@Column(name = "pricex", nullable = false, comment = "折扣价格", defaultValue = "0")
	private double pricex;

	@Column(name = "create_time", nullable = false, comment = "创建时间", defaultValue = "2018-01-01 00:00:00")
	private Date createTime;

	@Column(name = "modify_time", comment = "修改时间", defaultValue = "2018-01-01 00:00:00")
	private Date modifyTime;

	@Json
	@Column(name = "attr", nullable = false, comment = "属性Json", defaultValue = "{}")
	private ItemAttr attr;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getPlayerId() {
		return playerId;
	}

	public void setPlayerId(long playerId) {
		this.playerId = playerId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public boolean isCanUse() {
		return canUse;
	}

	public void setCanUse(boolean canUse) {
		this.canUse = canUse;
	}

	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}

	public double getPricex() {
		return pricex;
	}

	public void setPricex(double pricex) {
		this.pricex = pricex;
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

	public ItemAttr getAttr() {
		return attr;
	}

	public void setAttr(ItemAttr attr) {
		this.attr = attr;
	}
}