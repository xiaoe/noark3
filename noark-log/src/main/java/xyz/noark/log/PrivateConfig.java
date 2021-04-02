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

import xyz.noark.log.pattern.FormatterFactory;
import xyz.noark.log.pattern.PatternFormatter;

import java.util.List;

/**
 * 每个Logger对象都有一个自己的配置
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.4.3
 */
class PrivateConfig {
    /**
     * Int类型的日志等级
     */
    private final int intLevel;
    /**
     * 所有输出终端
     */
    private final List<Appender> appenderList;
    /**
     * 显示样式格式化
     */
    private final List<PatternFormatter> formatterList;
    /**
     * 是否输出类名和行号，这个状态不打开就算配置%F:%L也没有用哈...
     */
    private final boolean includeLocation;

    PrivateConfig(AbstractLogger logger, LogConfigurator configurator) {
        final LogConfig config = configurator.getConfig(logger.getName());
        this.intLevel = config.getLevel().getValue();
        this.appenderList = AppenderFactory.createList(config);
        this.formatterList = FormatterFactory.build(config.getLayoutPattern());

        // 配置了要显示文件或行号，那就为True
        this.includeLocation = formatterList.stream().anyMatch(PatternFormatter::isIncludeLocation);
    }

    /**
     * 获取Int类型的日志等级
     *
     * @return 日志等级
     */
    public int getIntLevel() {
        return intLevel;
    }

    /**
     * 是否记录类型和行号的状态
     *
     * @return 记录类型和行号的状态
     */
    public boolean isIncludeLocation() {
        return includeLocation;
    }

    /**
     * 处理日志信息.
     *
     * @param event 日志事件
     */
    public void processLogEvent(LogEvent event) {
        // 1. 先构建输出结果
        final char[] text = event.build(formatterList);
        // 2. 投放所有要显示的终端
        appenderList.forEach(v -> v.output(event, text));
    }
}