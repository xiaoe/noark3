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
package xyz.noark.log.pattern;

import java.util.LinkedList;
import java.util.List;

/**
 * 样式格式配置解析类
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.4.3
 */
class PatternParser {
    /**
     * 格式定义的转义字符：%
     */
    private static final char ESCAPE_CHAR = '%';
    /**
     * 计算长度所用进制
     */
    private static final int DECIMAL = 10;
    /**
     * 解析临时用的SB
     */
    private final StringBuilder tempSb = new StringBuilder(32);
    /**
     * 格式配置模板
     */
    private final String pattern;
    private final int patternLength;
    /**
     * 读指针
     */
    private int readIndex = 0;

    private PatternParser(String pattern) {
        this.pattern = pattern;
        this.patternLength = pattern.length();
    }

    static List<PatternFormatter> parseFormatterList(String pattern) {
        return new PatternParser(pattern).parseFormatterList();
    }

    List<PatternFormatter> parseFormatterList() {
        final List<PatternFormatter> formatterList = new LinkedList<>();

        // 原样输出的起始位置
        int literalIndex = 0;
        while (readIndex < patternLength) {
            char c = pattern.charAt(readIndex++);
            if (c == ESCAPE_CHAR) {
                // 发现%之前的格式先按固定输出提取
                this.finalizeLiteralPatternConverter(formatterList, literalIndex);

                // % + 格式修饰符 + 识别编码 + {编码的参数}
                FormattingInfo formattingInfo = this.extractFormattingInfo();
                String converterId = this.extractConverterId();
                String options = this.extractOptions();
                formatterList.add(FormatterFactory.create(converterId, formattingInfo, options));

                // 重置下标
                literalIndex = readIndex;
            }
        }
        // 最到后结束也要尝试按固定输出提取一下
        this.finalizeLiteralPatternConverter(formatterList, literalIndex);
        return formatterList;
    }

    private void finalizeLiteralPatternConverter(List<PatternFormatter> converterList, int literalIndex) {
        if (readIndex - 1 > literalIndex) {
            char[] array = new char[readIndex - 1 - literalIndex];
            pattern.getChars(literalIndex, readIndex - 1, array, 0);
            converterList.add(new LiteralPatternFormatter(array));
        }
    }

    private String extractOptions() {
        tempSb.setLength(0);
        if (readIndex < patternLength && pattern.charAt(readIndex) == '{') {
            readIndex++;
            while (readIndex < patternLength) {
                char c = pattern.charAt(readIndex++);
                if (c == '}') {
                    break;
                }
                tempSb.append(c);
            }
        }
        return tempSb.toString();
    }

    /**
     * 提取转换器
     */
    private String extractConverterId() {
        tempSb.setLength(0);
        while (readIndex < patternLength) {
            char c = pattern.charAt(readIndex);
            if (Character.isUnicodeIdentifierPart(c)) {
                tempSb.append(c);
                this.readIndex++;
            } else {
                break;
            }
        }
        return tempSb.toString();
    }

    /**
     * 提取格式化信息
     *
     * @return 格式化信息
     */
    private FormattingInfo extractFormattingInfo() {
        // 最小长度
        int minLength = 0;
        // 左对齐
        boolean leftAlign = false;
        // %date{yyyy-MM-dd HH:mm:ss.SSS} %-5level [%thread][%file:%line] - %msg%n
        while (readIndex < patternLength) {
            char c = pattern.charAt(readIndex);
            // 使用左对齐
            if (c == '-') {
                this.readIndex++;
                leftAlign = true;
            }
            // 最小长度
            else if (c >= '0' && c <= '9') {
                this.readIndex++;
                minLength = minLength * DECIMAL + c - '0';
            }
            // 配置结束，跳出循环
            else {
                break;
            }
        }
        // 构建一个FormattingInfo，如果都是默认值，那就使用默认的
        return FormattingInfo.getOrDefault(leftAlign, minLength);
    }
}
