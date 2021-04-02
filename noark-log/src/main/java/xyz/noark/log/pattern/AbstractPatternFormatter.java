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

import xyz.noark.log.LogEvent;

/**
 * 抽象的样式格式化实现
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.4.3
 */
public abstract class AbstractPatternFormatter implements PatternFormatter {
    protected final FormattingInfo formattingInfo;
    private final boolean skipFormattingInfo;

    public AbstractPatternFormatter(FormattingInfo formattingInfo, String options) {
        this.formattingInfo = formattingInfo;
        this.skipFormattingInfo = FormattingInfo.getDefault().equals(formattingInfo);
    }

    @Override
    public boolean isIncludeLocation() {
        return false;
    }

    @Override
    public void format(LogEvent event, StringBuilder toAppendTo) {
        if (skipFormattingInfo) {
            this.doFormat(event, toAppendTo);
        } else {
            this.formatWithInfo(event, toAppendTo);
        }
    }

    private void formatWithInfo(final LogEvent event, final StringBuilder toAppendTo) {
        final int startField = toAppendTo.length();
        this.doFormat(event, toAppendTo);
        // 修正
        formattingInfo.format(startField, toAppendTo);
    }

    /**
     * 具体记录行为
     *
     * @param event      一次日志记录
     * @param toAppendTo 记录目标
     */
    protected abstract void doFormat(LogEvent event, StringBuilder toAppendTo);
}
