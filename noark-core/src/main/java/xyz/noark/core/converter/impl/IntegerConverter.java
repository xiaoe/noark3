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
package xyz.noark.core.converter.impl;

import xyz.noark.core.annotation.TemplateConverter;
import xyz.noark.core.converter.AbstractConverter;

/**
 * Integer转化器.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.0
 */
@TemplateConverter({int.class, Integer.class})
public class IntegerConverter extends AbstractConverter<Integer> {

    @Override
    public Integer convert(String value) {
        // 常规数字
        try {
            return Integer.parseInt(value);
        }
        // 解析不出来，那就是一个骚的写法，比如为了可读性添加的'_'或','等
        catch (NumberFormatException e) {
            return Integer.parseInt(value.trim().replaceAll("_|,", ""));
        }
    }

    @Override
    public String buildErrorMsg() {
        return "不是一个int类型的值";
    }
}