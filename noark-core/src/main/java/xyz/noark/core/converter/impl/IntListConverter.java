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
package xyz.noark.core.converter.impl;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Map;

import xyz.noark.core.annotation.TemplateConverter;
import xyz.noark.core.converter.AbstractConverter;
import xyz.noark.core.lang.IntArrayList;
import xyz.noark.core.lang.IntList;
import xyz.noark.core.util.StringUtils;

/**
 * IntList转化器.
 *
 * @since 3.0
 * @author 小流氓(176543888@qq.com)
 */
@TemplateConverter({ IntList.class, IntArrayList.class })
public class IntListConverter extends AbstractConverter<IntList> {
	@Override
	public IntList convert(String value) {
		if (StringUtils.isEmpty(value)) {
			return new IntArrayList();
		}

		String[] array = StringUtils.split(value, ",");
		IntList result = new IntArrayList(array.length);
		Arrays.stream(array).forEach(v -> result.add(Integer.parseInt(v)));
		return result;
	}

	@Override
	public IntList convert(Field field, Map<String, String> data) {
		if (data.isEmpty()) {
			return new IntArrayList();
		}
		return new IntArrayList(data.values().stream().mapToInt(Integer::parseInt).toArray());
	}

	@Override
	public String buildErrorMsg() {
		return "数字类型的数组应该是以英文逗号分隔的，如：1,2,3,4";
	}
}