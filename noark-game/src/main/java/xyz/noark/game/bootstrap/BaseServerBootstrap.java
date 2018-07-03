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
package xyz.noark.game.bootstrap;

import java.util.Optional;

import javax.annotation.PostConstruct;

import xyz.noark.core.Modular;
import xyz.noark.core.annotation.orm.DataCheckAndInit;
import xyz.noark.core.network.PacketCodec;
import xyz.noark.game.template.ReloadManager;
import xyz.noark.network.NettyServer;
import xyz.noark.network.codec.json.SimpleJsonCodec;

/**
 * 一个默认的服务器启动引导类.
 *
 * @since 3.0
 * @author 小流氓(176543888@qq.com)
 */
public abstract class BaseServerBootstrap extends AbstractServerBootstrap {

	private NettyServer nettyServer;
	private Optional<Modular> dataModular;
	private Optional<Modular> eventModular;

	@Override
	protected void onStart() {
		// 1、DB检测与缓存初始化
		dataModular = modularManager.getModular("DataModular");
		dataModular.ifPresent(v -> initDataModular(v));

		// 2、重载所有策划模板数据.
		ioc.get(ReloadManager.class).reload(true);

		// 3、初始化方法...
		ioc.invokeCustomAnnotationMethod(PostConstruct.class);

		// 4、延迟事件动起来.
		eventModular = modularManager.getModular("EventModular");
		eventModular.ifPresent(v -> v.init());

		// HTTP服务

		// 对外网络...
		nettyServer = ioc.get(NettyServer.class);
		nettyServer.startup();
	}

	private void initDataModular(Modular modular) {
		modular.init();
		ioc.invokeCustomAnnotationMethod(DataCheckAndInit.class);
	}

	@Override
	protected PacketCodec getPacketCodec() {
		return new SimpleJsonCodec();
	}

	@Override
	protected void onStop() {
		// 停止对外网络
		nettyServer.shutdown();
		// 停止延迟任务调度
		eventModular.ifPresent(v -> v.destroy());

		// 等待所有任务处理完

		// 保存数据
		dataModular.ifPresent(v -> v.destroy());
	}
}