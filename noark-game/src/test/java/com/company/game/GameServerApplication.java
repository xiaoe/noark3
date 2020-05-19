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
package com.company.game;

import com.company.game.event.BuildingUpgradeEvent;
import xyz.noark.core.annotation.Controller;
import xyz.noark.core.annotation.controller.EventListener;
import xyz.noark.core.annotation.controller.ExecThreadGroup;
import xyz.noark.core.annotation.controller.PacketMapping;
import xyz.noark.core.network.Sender;
import xyz.noark.core.network.Session;
import xyz.noark.core.network.Session.State;
import xyz.noark.game.Noark;

import static xyz.noark.log.LogHelper.logger;

/**
 * 一个简单的服务器启动测试入口.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.0
 */
@Controller(threadGroup = ExecThreadGroup.ModuleThreadGroup)
public class GameServerApplication {

    public static void main(String[] args) {
        Noark.run(GameServerBootstrap.class, args);
    }

    @PacketMapping(opcode = 3)
    public void test3() {
        System.out.println("3333");
    }

    @PacketMapping(opcode = 1, state = State.CONNECTED)
    public void test(Session session, byte[] hello) {
        logger.info("收到协议:{}", new String(hello));
        session.send(1, "11111111111111111");
    }

    @EventListener(LoginEvent.class)
    public void handleEvent() {
        logger.info("处理事件........");
    }

    @EventListener(BuildingUpgradeEvent.class)
    public void handleBuildingUpgradeEvent() {
        logger.info("建筑升级时间到........");
    }
}