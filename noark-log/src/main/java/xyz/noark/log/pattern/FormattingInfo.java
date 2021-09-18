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

import java.util.Objects;

/**
 * 格式化信息
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.4.3
 */
final class FormattingInfo {
    private static final char[] SPACES = new char[]{' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '};
    private static final FormattingInfo DEFAULT = new FormattingInfo(false, 0);

    /**
     * 最小长度
     */
    private final int minLength;
    /**
     * 左对齐
     */
    private final boolean leftAlign;

    private FormattingInfo(final boolean leftAlign, final int minLength) {
        this.leftAlign = leftAlign;
        this.minLength = minLength;
    }

    public static FormattingInfo getDefault() {
        return DEFAULT;
    }

    public static FormattingInfo getOrDefault(boolean leftAlign, int minLength) {
        if (leftAlign == DEFAULT.leftAlign && minLength == DEFAULT.minLength) {
            return DEFAULT;
        } else {
            return new FormattingInfo(leftAlign, minLength);
        }
    }

    /**
     * 根据指定的长度和对齐方式调整缓冲区的内容
     *
     * @param startIndex 缓冲区起始位置
     * @param buffer     缓冲区
     */
    public void format(final int startIndex, final StringBuilder buffer) {
        final int rawLength = buffer.length() - startIndex;

        if (rawLength < minLength) {
            if (leftAlign) {
                final int fieldEnd = buffer.length();
                buffer.setLength(startIndex + minLength);

                for (int i = fieldEnd; i < buffer.length(); i++) {
                    buffer.setCharAt(i, ' ');
                }
            } else {
                int padLength = minLength - rawLength;

                final char[] paddingArray = SPACES;

                for (; padLength > paddingArray.length; padLength -= paddingArray.length) {
                    buffer.insert(startIndex, paddingArray);
                }

                buffer.insert(startIndex, paddingArray, 0, padLength);
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FormattingInfo)) {
            return false;
        }
        FormattingInfo that = (FormattingInfo) o;
        return minLength == that.minLength && leftAlign == that.leftAlign;
    }

    @Override
    public int hashCode() {
        return Objects.hash(minLength, leftAlign);
    }

    @Override
    public String toString() {
        return "FormattingInfo" +
                "[leftAlign=" +
                leftAlign +
                ", minLength=" +
                minLength +
                ']';
    }
}