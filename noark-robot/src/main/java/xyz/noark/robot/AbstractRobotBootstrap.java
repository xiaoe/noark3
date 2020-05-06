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
package xyz.noark.robot;

import xyz.noark.core.Modular;
import xyz.noark.game.bootstrap.AbstractServerBootstrap;
import xyz.noark.game.bt.BehaviorTree;
import xyz.noark.game.template.ReloadManager;

import javax.annotation.PostConstruct;
import java.util.Optional;

/**
 * 抽象的机器人启动引导类
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.4
 */
public abstract class AbstractRobotBootstrap extends AbstractServerBootstrap {
    protected Optional<Modular> eventModular;
    protected Optional<Modular> threadModular;

    @Override
    protected String getServerName() {
        return "robot";
    }

    @Override
    protected void onStart() {
        // 0、线程模型
        this.initThreadModular();

        // 1、重载所有策划模板数据.
        ioc.get(ReloadManager.class).reload(true);

        // 2、初始化方法...
        ioc.invokeCustomAnnotationMethod(PostConstruct.class);

        // 3、延迟事件动起来.
        this.initEventModular();
    }

    @Override
    public void start() {
        super.start();
        ioc.get(RobotManager.class).init(this);
    }

    /**
     * 构建机器人的AI.
     *
     * @param playerId 机器人ID
     * @return 机器人的AI
     */
    protected abstract BehaviorTree rebuildAi(String playerId);

    /**
     * 初始化事件模块
     */
    protected void initEventModular() {
        eventModular = modularManager.getModular(Modular.EVENT_MODULAR);
        eventModular.ifPresent(v -> v.init());
    }

    /**
     * 初始化线程模块
     */
    protected void initThreadModular() {
        threadModular = modularManager.getModular(Modular.THREAD_MODULAR);
        threadModular.ifPresent(v -> v.init());
    }

    @Override
    protected void onStop() {
        // 停止延迟任务调度
        if (eventModular != null) {
            eventModular.ifPresent(v -> v.destroy());
        }
        // 等待所有任务处理完
        if (threadModular != null) {
            threadModular.ifPresent(v -> v.destroy());
        }
    }
}
