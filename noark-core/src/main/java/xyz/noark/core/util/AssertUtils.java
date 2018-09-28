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
package xyz.noark.core.util;

import static xyz.noark.log.LogHelper.logger;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import xyz.noark.core.exception.ServerBootstrapException;

/**
 * 断言工具类.
 *
 * @since 3.2
 * @author 小流氓(176543888@qq.com)
 */
public class AssertUtils {

	/**
	 * 断言一个集合元素中一个字符串属性有没有超出最大长度.
	 * <p>
	 * 主要用于检测模板配置，如果过长提前通知处理，不然存不进数据库麻烦就来了
	 *
	 * @param <T> 集合中元素类型
	 * @param list 指定集合
	 * @param stringFunc 集合中元素对象中一个字符串属性
	 * @param maxLength 最大长度
	 * @param tips 如果超出最大长度的提示文字
	 * 
	 *            <pre>
	 * 例如：道具模板ID超出了最大长度限制, templateId={}, maxLength={}
	 *            </pre>
	 * 
	 * @throws ServerBootstrapException 如果指定集合中有超出限制的元素则会抛出此异常
	 */
	public static <T> void assertStringMaxLength(Collection<T> list, Function<T, String> stringFunc, int maxLength, String tips) {
		List<T> result = list.stream().filter(v -> stringFunc.apply(v).length() > maxLength).collect(Collectors.toList());
		if (!result.isEmpty()) {
			result.forEach(v -> logger.warn(tips, stringFunc.apply(v), maxLength));
			throw new ServerBootstrapException("断言集合中字符串属性长度超出了最大限制，请速速处理...");
		}
	}
}