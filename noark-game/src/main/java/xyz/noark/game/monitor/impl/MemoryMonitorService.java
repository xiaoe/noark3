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
package xyz.noark.game.monitor.impl;

import xyz.noark.core.util.FileUtils;
import xyz.noark.game.monitor.AbstractMonitorService;

import java.util.concurrent.TimeUnit;

import static xyz.noark.log.LogHelper.logger;

/**
 * 内存监控服务.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.1
 */
public class MemoryMonitorService extends AbstractMonitorService {

    public MemoryMonitorService() {
    }

    @Override
    protected long getInitialDelay() {
        return 60;
    }

    @Override
    protected long getDelay() {
        return 60;
    }

    @Override
    protected TimeUnit getUnit() {
        return TimeUnit.SECONDS;
    }

    @Override
    protected void exe() throws Exception {
        Runtime runtime = Runtime.getRuntime();
        // 最大可用内存
        long maxMemory = runtime.maxMemory();
        // 已分配内存
        long totalMemory = runtime.totalMemory();
        // 已分配内存中的剩余空间
        long useMemory = totalMemory - runtime.freeMemory();
        // 最大可用内存
        long usableMemory = maxMemory - useMemory;
        logger.info("最大堆内存={}, 已分配={}, 已使用={}, 还可用={}", FileUtils.readableFileSize(maxMemory), FileUtils.readableFileSize(totalMemory), FileUtils.readableFileSize(useMemory), FileUtils.readableFileSize(usableMemory));
    }
}