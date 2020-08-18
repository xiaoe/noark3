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
package xyz.noark.benchmark;

import static xyz.noark.log.LogHelper.logger;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 性能基准测试.
 *
 * @author 江贵龙[41157121@qq.com]
 * @author 小流氓[176543888@qq.com]
 * @since 3.0
 */
public class Benchmark {
    private static final int WARM_UP_TIMES = 100;
    private final int times;

    public Benchmark() {
        this(100_0000);
    }

    public Benchmark(int times) {
        this.times = times;
        logger.debug("Benchmark Test times:{}",times);
    }

    /**
     * 单线程压测做任务接口.
     *
     * @param name     任务名称
     * @param callback 任务
     * @throws Exception 做任务的过程中可能会抛出的异常
     */
    public void doSomething(String name, BenchmarkCallback callback) throws Exception {
        this.doSomething(times, name, callback);
    }

    /**
     * 多线程压测做任务接口.
     *
     * @param times    循环几次
     * @param name     任务名称
     * @param callback 任务
     * @throws Exception 做任务的过程中可能会抛出的异常
     */
    public void doSomething(int times, String name, BenchmarkCallback callback) throws Exception {
        this.doSomething(1, times, name, callback);
    }

    /**
     * 多线程并发压测做任务接口.
     *
     * @param thread   多少线程的并发
     * @param times    循环几次
     * @param name     任务名称
     * @param callback 任务
     * @throws Exception 做任务的过程中可能会抛出的异常
     */
    public void doSomething(int thread, int times, String name, BenchmarkCallback callback) throws Exception {
        ExecutorService pool = Executors.newFixedThreadPool(thread, new BenchmarkThreadFactory(name));
        // 预热
        for (int i = 0; i < WARM_UP_TIMES; i++) {
            callback.doSomething();
        }

        // 计时，跑测试
        CountDownLatch latch = new CountDownLatch(times);
        Instant startTime = Instant.now();
        if (thread == 1) {
            for (int i = 0; i < times; i++) {
                callback.doSomething();
            }
        } else {
            for (int i = 0; i < times; i++) {
                pool.execute(() -> {
                    try {
                        callback.doSomething();
                        latch.countDown();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
            try {
                latch.await();
            } catch (InterruptedException e) {
            	logger.error(e.getMessage(),e);
            }
        }
        Instant endTime = Instant.now();
        long interval = Duration.between(startTime, endTime).toMillis();
        logger.debug("{},{} - total= {} ms,times={}, speed= {} ms", Thread.currentThread().getName(), name, interval, times, String.format("%.6f", interval * 1d / times));
    }
}