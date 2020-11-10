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
package xyz.noark.core.lang;

import xyz.noark.core.converter.impl.LocalTimeSectionConverter;

import java.time.LocalDateTime;

/**
 * 时间范围,主要用于指定时间区间的条件判定.
 * <p>
 * 格式：[年][月][日][星期][时间]<br>
 * 例：<br>
 * [*][*][*][*][00:00-24:00]<br>
 * [2020][5-6][11,12,15-19][w1,w5-w7][12:00-13:00]<br>
 * 前4个区，都可以使用逗号和连接号来实现多段效果, 判定规则是所有段内条件都满足
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.4
 */
public class TimeRange implements ValidTime {
    /**
     * 年
     */
    private IntRange year;
    /**
     * 月
     */
    private IntRange month;
    /**
     * 日
     */
    private IntRange day;
    /**
     * 星期
     */
    private IntRange dayOfWeek;
    /**
     * 时间区间
     */
    private LocalTimeSection timeSection;

    public TimeRange(String expression) {
        BracketParser parser = new BracketParser(expression);
        this.year = new IntRange(parser.readString());
        this.month = new IntRange(parser.readString());
        this.day = new IntRange(parser.readString());
        this.dayOfWeek = new IntRange(parser.readString());
        this.timeSection = new LocalTimeSectionConverter().convert(parser.readString());
    }

    @Override
    public boolean isValid(LocalDateTime time) {
        // 年份
        if (!year.contains(time.getYear())) {
            return false;
        }
        // 月份
        if (!month.contains(time.getMonthValue())) {
            return false;
        }
        // 日
        if (!day.contains(time.getDayOfMonth())) {
            return false;
        }
        // 星期
        if (!dayOfWeek.contains(time.getDayOfWeek().getValue())) {
            return false;
        }
        // 时间
        return timeSection.isValid(time.toLocalTime());
    }
}