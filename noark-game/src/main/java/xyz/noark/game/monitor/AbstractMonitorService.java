/*
 * Copyright © 2018 www.noark.xyz All Rights Reserved.
 *
 * 感谢您选择Noark框架，希望我们的努力能为您提供一个简单、易用、稳定的服务器端框架 ！
 * 除非符合Noark许可协议，否则不得使用该文件，您可以下载许可协议文件：
 *
 *        http://www.noark.xyz/LICENSE
 *
 * 1.未经许可，任何公司及个人不得以任何方式或理由对本框架进行修改、使用和传播;
 * 2.禁止在本项目或任何子项目的基础上发展任何派生版本、修改版本或第三方版本;
 * 3.无论你对源代码做出任何修改和改进，版权都归Noark研发团队所有，我们保留所有权利;
 * 4.凡侵犯Noark版权等知识产权的，必依法追究其法律责任，特此郑重法律声明！
 */
package xyz.noark.game.monitor;

import java.util.concurrent.TimeUnit;

import static xyz.noark.log.LogHelper.logger;

/**
 * 监控服务接口...
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.1
 */
public abstract class AbstractMonitorService implements Runnable {

    /**
     * 延迟多少执行.
     *
     * @return 延迟时间
     */
    protected abstract long getInitialDelay();

    /**
     * 间隔多少执行.
     *
     * @return 间隔时间
     */
    protected abstract long getDelay();

    /**
     * 时间单位.
     *
     * @return 返回延迟或间隔的时间单位.
     */
    protected abstract TimeUnit getUnit();

    @Override
    public void run() {
        try {
            this.exe();
        } catch (Exception e) {
            logger.info("监控异常.", e);
        }
    }

    /**
     * 执行逻辑.
     *
     * @throws Exception 可能会出现一些不常见的异常.
     */
    protected abstract void exe() throws Exception;
}