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
 * Column注解标记表示所持久化属性所映射表中的字段.
 *
 * @since 3.0
 * @author 小流氓[176543888@qq.com]
 */
@Documented
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Column {
	/**
	 * @return 属性对应数据库中列的名称.
	 */
	String name();

	/**
	 * @return nullable属性表示该字段是否可以为null值，默认为true。
	 */
	boolean nullable() default true;

	/**
	 * @return length属性表示字段的长度，当字段的类型为varchar时，该属性才有效，默认为255个字符。
	 */
	int length() default 255;

	/**
	 * precision属性和scale属性表示精度，当字段类型为double时，precision表示数值的总长度， scale表示小数点所占的位数。
	 * 
	 * @return 数值的总长度
	 */
	int precision() default 15;

	/**
	 * @see Column#precision
	 * 
	 * @return 小数点所占的位数
	 */
	int scale() default 5;

	/**
	 * @return comment表示注释，仅在创建表时起作用.
	 */
	String comment() default "";

	/**
	 * 建表时的默认值。
	 * 
	 * @return 如果有此属性，则在生成建表语句时添加此默认值
	 */
	String defaultValue() default "";
}