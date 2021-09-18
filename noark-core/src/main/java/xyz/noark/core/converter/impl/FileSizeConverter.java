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
import xyz.noark.core.lang.FileSize;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * FileSize转化器.
 * <p>这个计算方案来源Log4j2的源码</p>
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.4.3
 */
@TemplateConverter(FileSize.class)
public class FileSizeConverter extends AbstractConverter<FileSize> {
    /**
     * 表达式的正确性检测正则
     */
    private static final Pattern VALUE_PATTERN =
            Pattern.compile("([0-9]+([\\.,][0-9]+)?)\\s*(|K|M|G)B?", Pattern.CASE_INSENSITIVE);

    private static final long KB = 1024;
    private static final long MB = KB * KB;
    private static final long GB = KB * MB;

    private static final String UNITS_KB = "K";
    private static final String UNITS_MB = "M";
    private static final String UNITS_GB = "G";

    @Override
    public FileSize convert(String string) throws Exception {
        /*
         * 将字符串转换为字节数。
         * <p>字符串由浮点值后跟K、 M或G分别表示千字节、兆字节和千兆字节</p>
         * 这个缩写KB、MB和GB也可以接受<br>
         * 匹配不区分大小写
         *
         * @param string       要转换的字符串
         * @param defaultValue 在解析时检测到问题时的默认值
         * @return 字符串所表示的字节值
         */
        final Matcher matcher = VALUE_PATTERN.matcher(string.trim());
        // 验证输入
        if (matcher.matches()) {
            // 获取双精度值
            final double value = Double.parseDouble(matcher.group(1));
            // 获取指定的单位
            final String units = matcher.group(3);
            if (units.isEmpty()) {
                return new FileSize(value);
            } else if (UNITS_KB.equalsIgnoreCase(units)) {
                return new FileSize(value * KB);
            } else if (UNITS_MB.equalsIgnoreCase(units)) {
                return new FileSize(value * MB);
            } else if (UNITS_GB.equalsIgnoreCase(units)) {
                return new FileSize(value * GB);
            } else {
                throw new IllegalExpressionException("FileSize的单位只能是(|K|M|G)B?：" + units);
            }
        }
        throw new IllegalExpressionException("FileSize的格式验证失败:" + string);
    }

    @Override
    public String buildErrorMsg() {
        return "不是一个FileSize类型的值";
    }
}