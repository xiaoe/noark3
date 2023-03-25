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
package xyz.noark.log;

import java.time.format.DateTimeParseException;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 日志保留多少天数
 *
 * @author 小流氓[176543888@qq.com]
 */
public final class LogDay {
    private final int days;

    private LogDay(int days) {
        this.days = days;
    }

    public int getDays() {
        return days;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        return days == ((LogDay) o).days;
    }

    @Override
    public int hashCode() {
        return Objects.hash(days);
    }

    @Override
    public String toString() {
        return "LogDay{" + "days=" + days + '}';
    }

    //-----------------------------------------------------------------------
    private static final Pattern PATTERN = Pattern.compile("(?:([-+]?[0-9]+)Y)?(?:([-+]?[0-9]+)W)?(?:([-+]?[0-9]+)D)?", Pattern.CASE_INSENSITIVE);

    public static LogDay parse(CharSequence text) {
        Objects.requireNonNull(text, "text");
        Matcher matcher = PATTERN.matcher(text);
        if (matcher.matches()) {
            String yearMatch = matcher.group(1);
            String weekMatch = matcher.group(2);
            String dayMatch = matcher.group(3);
            if (yearMatch != null || dayMatch != null || weekMatch != null) {
                try {
                    int years = parseNumber(text, yearMatch);
                    int weeks = parseNumber(text, weekMatch);
                    int days = parseNumber(text, dayMatch);
                    days = Math.addExact(days, Math.multiplyExact(years, 365));
                    days = Math.addExact(days, Math.multiplyExact(weeks, 7));
                    return new LogDay(days);
                } catch (NumberFormatException ex) {
                    throw new DateTimeParseException("Text cannot be parsed to a Period", text, 0, ex);
                }
            }
        }
        throw new DateTimeParseException("Text cannot be parsed to a Period", text, 0);
    }

    private static int parseNumber(CharSequence text, String str) {
        if (str == null) {
            return 0;
        }
        try {
            return Integer.parseInt(str);
        } catch (ArithmeticException ex) {
            throw new DateTimeParseException("Text cannot be parsed to a LogDay, eg:1Y2W3D", text, 0, ex);
        }
    }
}