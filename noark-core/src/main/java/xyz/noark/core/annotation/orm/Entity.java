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
package xyz.noark.core.annotation.orm;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Entity注解是用来标注一个Java类为实体类.
 * <p>
 * 当一个Java类没有Entity注解时，就认为他不是一个实体对象. <br>
 * 当实体类没有此注解时会抛出 NoEntityException异常.
 *
 * @since 3.0
 * @author 小流氓[176543888@qq.com]
 */
@Documented
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface Entity {

	/**
	 * 返回当前实体类的抓取策略.
	 * 
	 * @return 返回配置的抓取策略，默认值为什么用，什么时候初始化.
	 */
	FetchType fetch() default FetchType.USE;

	/**
	 * 抓取策略.
	 * <p>
	 * 1.启动服务器的时候，初始化当前实体数据.<br>
	 * 2.登录游戏的时候，初始化当前实体数据.<br>
	 * 3.什么时候用，什么时候初始化当前实体数据.<br>
	 * 
	 * @author 小流氓[176543888@qq.com]
	 */
	public enum FetchType {
		/**
		 * 启动服务器的时候，初始化当前实体数据.
		 */
		START,
		/**
		 * 什么时候用，什么时候初始化当前实体数据.
		 */
		USE;
	}
}