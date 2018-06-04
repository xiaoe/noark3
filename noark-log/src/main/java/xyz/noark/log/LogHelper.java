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
package xyz.noark.log;

/**
 * 日志辅助类.
 * <p>
 * 这个类没有什么用，只是利用静态导入功能，显得比较装逼.<br>
 * 
 * <pre>
 * import static xyz.noark.log.LogHelper.logger;
 * </pre>
 *
 * @since 3.0
 * @author 小流氓(176543888@qq.com)
 */
public class LogHelper {
	/**
	 * 默认的那个Logger实现
	 * 
	 * <pre>
	 * logger.debug("记录一条{}级别的日志", "debug");<br>
	 * logger.info("记录一条{}级别的日志", "info");<br>
	 * logger.warn("记录一条警告级别的日志");<br>
	 * logger.error("记录一条错误级别的日志{}", new RuntimeException("我是异常..."));<br>
	 * </pre>
	 */
	public static final Logger logger = new NoarkLogger();
}