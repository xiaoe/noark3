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
package xyz.noark.core.ioc.definition.field;

import xyz.noark.core.converter.ConvertManager;
import xyz.noark.core.converter.Converter;
import xyz.noark.core.env.EnvConfigHolder;
import xyz.noark.core.exception.ConvertException;
import xyz.noark.core.exception.UnrealizedException;
import xyz.noark.core.ioc.IocMaking;
import xyz.noark.core.util.FieldUtils;
import xyz.noark.core.util.StringUtils;

import java.lang.reflect.Field;

/**
 * Value方式注入配置参数.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.0
 */
public class ValueFieldDefinition extends DefaultFieldDefinition {
    /**
     * 对应配置文件中的Key...
     */
    private final String key;

    public ValueFieldDefinition(Field field, String key) {
        super(field, false);
        this.key = key;
    }

    /**
     * 配置参数注入，如果没找到则忽略，使用默认值...
     */
    @Override
    public void injection(Object single, IocMaking making) {
        String value = EnvConfigHolder.getString(key);
        if (StringUtils.isNotEmpty(value)) {
            Converter<?> converter = ConvertManager.getInstance().getConverter(field.getType());
            if (converter != null) {
                try {
                    FieldUtils.writeField(single, field, converter.convert(field, value));
                } catch (Exception e) {
                    throw new ConvertException(single.getClass().getName() + " >> " + field.getName() + " >> " + value + "-->" + converter.buildErrorMsg(), e);
                }
            } else {
                throw new UnrealizedException("类：" + single.getClass().getName() + "中的属性：" + field.getName() + "类型未实现此转化器");
            }
        }
    }
}