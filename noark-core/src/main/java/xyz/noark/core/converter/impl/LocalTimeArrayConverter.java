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
import xyz.noark.core.exception.ConvertException;
import xyz.noark.core.lang.LocalTimeArray;
import xyz.noark.core.util.StringUtils;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 时间数组转化器.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.3.9
 */
@TemplateConverter(LocalTimeArray.class)
public class LocalTimeArrayConverter extends AbstractConverter<LocalTimeArray> {

    @Override
    public String buildErrorMsg() {
        return "格式\t08:00,16:00,22:00";
    }

    @Override
    public LocalTimeArray convert(String value) throws Exception {
        String[] array = StringUtils.split(value, ",");
        List<LocalTime> result = new ArrayList<>(array.length);
        for (String time : array) {
            String[] ts = StringUtils.split(time, ":");
            // 只有两个，那就是时分
            if (ts.length == 2) {
                result.add(LocalTime.of(Integer.parseInt(ts[0]), Integer.parseInt(ts[1])));
            }
            // 如果是3个的话，就当他是时分秒
            else if (ts.length == 3) {
                result.add(LocalTime.of(Integer.parseInt(ts[0]), Integer.parseInt(ts[1]), Integer.parseInt(ts[2])));
            }
            // 非法情况，异常提示
            else {
                throw new ConvertException("时间配置2位'08:00,12:00,22:00'或3位'08:00:00,12:00:00,22:00:00'");
            }
        }
        return new LocalTimeArray(result.toArray(new LocalTime[]{}));
    }
}