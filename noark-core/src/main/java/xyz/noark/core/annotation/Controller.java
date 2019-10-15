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
package xyz.noark.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import xyz.noark.core.annotation.controller.ExecThreadGroup;
import xyz.noark.core.util.StringUtils;

/**
 * Controller注解用来标识一个消息入口处理类.
 * <p>
 * 消息控制器，主要作用就是为每个模块接口消息处理的入口.<br>
 * 这个注解所标识的类，不会被其他类所注入，只会装配此类，但不会有别的类依赖于他.
 *
 * @since 3.0
 * @author 小流氓(176543888@qq.com)
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Controller {

	/**
	 * 标识这个协议控制中的入口方法由哪个线程组调用.
	 * <p>
	 * 默认转化为串型执行队列线程
	 * 
	 * @return 执行线程组.
	 */
	ExecThreadGroup threadGroup() default ExecThreadGroup.QueueThreadGroup;

	/**
	 * 串行执行的队列ID
	 * 
	 * @return 队列ID
	 * @since 3.4
	 */
	String value() default StringUtils.EMPTY;
}