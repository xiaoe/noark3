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
import xyz.noark.core.exception.IllegalExpressionException;
import xyz.noark.core.lang.Point;
import xyz.noark.core.util.IntUtils;
import xyz.noark.core.util.StringUtils;

/**
 * Point转化器.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.4
 */
@TemplateConverter(Point.class)
public class PointConverter extends AbstractConverter<Point> {

    @Override
    public Point convert(String value) {
        String[] array = StringUtils.split(value, ",");
        if (array.length == IntUtils.NUM_2) {
            return Point.valueOf(Integer.parseInt(array[0]), Integer.parseInt(array[1]));
        } else {
            throw new IllegalExpressionException("坐标表达式格式错误(x,y)：" + value);
        }
    }

    @Override
    public String buildErrorMsg() {
        return "不是一个Point类型的值(x,y)";
    }
}