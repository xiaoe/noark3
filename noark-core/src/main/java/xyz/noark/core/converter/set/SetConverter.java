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
package xyz.noark.core.converter.set;

import xyz.noark.core.annotation.TemplateConverter;
import xyz.noark.core.converter.Converter;
import xyz.noark.core.util.MapUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Set转化器.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.4.9
 */
@TemplateConverter(Set.class)
public class SetConverter extends AbstractSetConverter implements Converter<Set<Object>> {

    @Override
    protected Set<Object> createCollection(int length) {
        return length > 0 ? new HashSet<>(MapUtils.calculateInitialCapacity(length)) : Collections.emptySet();
    }

    @Override
    public Set<Object> convert(Field field, String value) throws Exception {
        return Collections.unmodifiableSet((Set<?>) super.convert(field, value));
    }

    @Override
    public Set<Object> convert(Parameter parameter, String value) throws Exception {
        return Collections.unmodifiableSet((Set<?>) super.convert(parameter, value));
    }

    @Override
    public Set<Object> convert(Field field, Map<String, String> data) throws Exception {
        return Collections.unmodifiableSet((Set<?>) super.convert(field, data));
    }

    @Override
    public String buildErrorMsg() {
        return super.buildErrorMsg();
    }
}
