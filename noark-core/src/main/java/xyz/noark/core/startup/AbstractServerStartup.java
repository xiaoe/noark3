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
package xyz.noark.core.startup;

import static xyz.noark.log.LogHelper.logger;

import javax.annotation.PostConstruct;

import xyz.noark.core.ioc.NoarkIoc;

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
 * @since 3.0
 * @author 小流氓(176543888@qq.com)
 */
public abstract class AbstractServerStartup implements ServerStartup {
	protected NoarkIoc ioc;// IOC容器

	// 启动服务时，添加一个停机守护线程，用于清理异常情况.
	public AbstractServerStartup() {
		Runtime.getRuntime().addShutdownHook(new ShutdownHook(this));
	}

	@Override
	public void start() {
		logger.info("starting {} service...", this.getServerName());
		long startTime = System.nanoTime();
		try {
			// 启动IOC容器
			this.ioc = new NoarkIoc(this.getClass().getPackage().getName());

			// DB

			// 载入策划配置模板

			// 初始化方法...
			ioc.invokeCustomAnnotationMethod(PostConstruct.class);// 数据库初始化完，执行初始化注解

			this.onStart();

			// HTTP服务

			// 对外网络...

			float interval = (System.nanoTime() - startTime) / 1000_000f;
			logger.info("{} is running, interval={} ms", this.getServerName(), interval);
			System.out.println(this.getServerName() + " is running, interval=" + interval + " ms");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("failed to starting service:{}", this.getServerName(), e);
			System.exit(1);
		}
	}

	/**
	 * @return 返回当前服务器名称.
	 */
	protected abstract String getServerName();

	protected abstract void onStart();

	@Override
	public void stop() {
		// TODO Auto-generated method stub

	}

}
