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
package com.company.test.module.login;

import static xyz.noark.log.LogHelper.logger;

import com.company.test.module.login.event.OnlineEvent;
import com.company.test.proto.json.LoginGame_CS;
import com.company.test.proto.json.LoginGame_SC;

import xyz.noark.core.annotation.Controller;
import xyz.noark.core.annotation.controller.ExecThreadGroup;
import xyz.noark.core.annotation.controller.PacketMapping;
import xyz.noark.core.event.EventBus;
import xyz.noark.core.network.Session.State;
import xyz.noark.network.NettySession;

/**
 * 登录入口.
 *
 * @since 3.0
 * @author 小流氓(176543888@qq.com)
 */
@Controller(threadGroup = ExecThreadGroup.ModuleThreadGroup)
public class LoginController {
	/**
	 * 登录游戏（第一个封包）
	 */
	@PacketMapping(opcode = 101, state = State.CONNECTED)
	public void loginGame(NettySession session, LoginGame_CS packet) {
		logger.info("玩家登录 username={}", packet.getUsername());
		// 登录成功...
		if (packet.getUsername().equalsIgnoreCase(packet.getPassword())) {
			session.setPlayerId(Long.parseLong(packet.getUsername()));
		}

		EventBus.publish(new OnlineEvent(Long.parseLong(packet.getUsername())));

		LoginGame_SC result = new LoginGame_SC();
		result.setPlayerId(Long.parseLong(packet.getUsername()));
		session.send(1001, result);
	}
}