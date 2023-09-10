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
package xyz.noark.game.bootstrap;

import xyz.noark.core.ModularManager;
import xyz.noark.core.env.EnvConfigHolder;
import xyz.noark.core.exception.ServerBootstrapException;
import xyz.noark.core.ioc.NoarkIoc;
import xyz.noark.core.network.PacketCodec;
import xyz.noark.core.network.PacketCodecHolder;
import xyz.noark.core.thread.NamedThreadFactory;
import xyz.noark.core.thread.TraceIdFactory;
import xyz.noark.core.util.*;
import xyz.noark.game.NoarkConstant;
import xyz.noark.log.LogManager;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static xyz.noark.log.LogHelper.logger;

/**
 * 抽象的启动服务类.
 * <p>
 * 自动初始化IOC容器，所以需要所有模块都在启动类的子目录下.<br>
 * <b>注意：此类的实现类位置很重要...</b>
 *
 * <pre>
 * this.ioc = new NoarkIoc(this.getClass().getPackage().getName());
 * </pre>
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.0
 */
public abstract class AbstractServerBootstrap implements ServerBootstrap {
    /**
     * IOC容器
     */
    protected NoarkIoc ioc;
    protected ModularManager modularManager;
    /**
     * PID文件名称
     */
    private String pidFileName;

    /**
     * 启动服务时，添加一个停机守护线程，用于清理异常情况.
     */
    public AbstractServerBootstrap() {
        Runtime.getRuntime().addShutdownHook(new ShutdownHook(this));
    }

    /**
     * 返回当前服务器名称.
     *
     * @return 服务器名称
     */
    protected abstract String getServerName();

    @Override
    public void start() {
        logger.info("starting {} service...", this.getServerName());
        long startTime = System.nanoTime();
        try {
            // 启动IOC容器
            String profile = EnvConfigHolder.getString(NoarkConstant.NOARK_PROFILES_ACTIVE);
            this.ioc = new NoarkIoc(profile, this.getClass().getPackage().getName());
            this.modularManager = ioc.get(ModularManager.class);

            // 服务器启动之前的逻辑...
            this.onBeginStart();

            // 启动逻辑
            this.onStart();

            float interval = DateUtils.formatNanoTime(System.nanoTime() - startTime);
            logger.info("{} is running, interval={} ms", this.getServerName(), interval);

            // 打印启动信息
            this.printStartInfo(interval);

            // 打印Banner
            if (this.showBanner()) {
                FileUtils.loadFileText(bannerFileName()).ifPresent(this::printBanner);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("failed to starting service:{}, exception={}", this.getServerName(), e);
            System.exit(1);
        }
    }

    protected void printStartInfo(float interval) {
        System.out.println(this.getServerName() + " is running, interval=" + interval + " ms");
    }

    protected void onBeginStart() {
        PacketCodecHolder.setPacketCodec(getPacketCodec());

        // 开启停服信号功能
        if (shutdownSignalEnabled()) {
            logger.warn("已启用回车停机功能");
            ExecutorService singleThreadPool = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(1), new NamedThreadFactory("安全停服：测试启用"));
            singleThreadPool.execute(() -> {
                try {
                    final int read = System.in.read();
                    TraceIdFactory.initFixedTraceIdByStopServer();
                    logger.debug("收到信息：{}", read);
                } catch (Exception e) {
                    logger.error("{}", e);
                }
                System.exit(0);
            });
        }

        // 写入PID文件....
        this.pidFileName = EnvConfigHolder.getString(NoarkConstant.PID_FILE);
        this.createPidFile();
    }

    protected boolean shutdownSignalEnabled() {
        // 如果开启了Debug模式，那就启动一个回车停服功能啊，不管什么系统
        String signal = EnvConfigHolder.getString(NoarkConstant.SHUTDOWN_SIGNAL_ENABLED);
        boolean flag;
        // 默认情况：Window系统默开启，Linux默认关闭
        if (StringUtils.isEmpty(signal)) {
            flag = SystemUtils.IS_OS_WINDOWS;
        }
        // 如果配置了当前值，则强制使用配置值
        else {
            flag = BooleanUtils.toBoolean(signal);
        }
        return flag;
    }

    /**
     * 尝试写入PID到文件
     */
    protected void createPidFile() {
        if (StringUtils.isNotEmpty(pidFileName)) {
            // 清理路径的方式预防路径遍历的威胁
            Path pidPath = Paths.get(pidFileName).normalize();

            // PID文件已存在
            if (Files.exists(pidPath)) {
                this.pidFileName = null;
                String absolutePath = pidPath.toFile().getAbsolutePath();
                throw new ServerBootstrapException("PID文件已存在，如果异常停服，请手动删除PID文件 >> " + absolutePath);
            }

            try {
                final File pidFile = pidPath.toFile();
                // 创建文件
                if (FileUtils.createNewFile(pidFile)) {
                    logger.debug("PID文件创建成功. file={}", pidFile.getAbsolutePath());

                    // 写入PID
                    try (FileWriter fileWriter = new FileWriter(pidFile, false)) {
                        fileWriter.write(SystemUtils.getPidStr());
                        fileWriter.flush();
                    }
                }
                // 创建失败
                else {
                    throw new ServerBootstrapException("PID文件创建失败，请确认一下权限是否正常 >> " + pidFile.getAbsolutePath());
                }
            } catch (IOException e) {
                throw new ServerBootstrapException("PID文件创建失败，请确认一下权限是否正常 >> " + pidFileName, e);
            }
        }
    }

    /**
     * 自定义封包结构需要重写当前方法.
     *
     * @return 封包的编解码
     */
    protected abstract PacketCodec getPacketCodec();

    /**
     * 启动逻辑.
     */
    protected abstract void onStart();

    @Override
    public void stop() {
        logger.info("stopping service: {}", this.getServerName());
        try {
            logger.info("goodbye {}", this.getServerName());
            // 打印停止事件
            this.printStopInfo();
            // 停止逻辑
            this.onStop();
        } catch (Exception e) {
            logger.error("failed to stopping service:{}", this.getServerName(), e);
        } finally {
            // IOC容器销毁
            if (ioc != null) {
                ioc.destroy();
            }
            // 日志框架Shutdown
            LogManager.shutdown();

            // 删除PID文件
            this.deletePidFile();
        }
    }

    protected void printStopInfo() {
        System.out.println("goodbye " + this.getServerName());
    }

    /**
     * 停服时尝试删除PID文件
     */
    protected void deletePidFile() {
        if (StringUtils.isNotEmpty(pidFileName)) {
            File pidFile = new File(pidFileName);
            if (pidFile.exists() && !pidFile.delete()) {
                logger.warn("PID文件删除失败，请手动确认并删除.");
            }
        }
    }

    /**
     * 关闭逻辑.
     */
    protected abstract void onStop();

    /**
     * 显示Banner.
     *
     * @return 如果显示返回true，否则返回false.
     */
    protected boolean showBanner() {
        return true;
    }

    /**
     * Banner文件名称.
     * <p>
     * 重载此方法可以替换默认的输出Banner图案
     *
     * @return Banner文件名称
     */
    protected String bannerFileName() {
        return NoarkConstant.BANNER_DEFAULT;
    }

    /**
     * 打印Banner图案.
     *
     * @param text Banner图案
     */
    protected void printBanner(String text) {
        logger.info(EnvConfigHolder.fillExpression(text));
    }
}