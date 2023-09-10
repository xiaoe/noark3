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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 样式格式工厂类.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.4.3
 */
public class FormatterFactory {
    private static final Map<String, Class<? extends PatternFormatter>> formatterImplClassMap = new HashMap<>();

    static {
        // 日期
        Arrays.asList("d", "date").forEach(v -> formatterImplClassMap.put(v, DatePatternFormatter.class));

        // 等级
        Arrays.asList("p", "level").forEach(v -> formatterImplClassMap.put(v, LevelPatternFormatter.class));

        // 线程
        Arrays.asList("t", "tn", "thread", "threadName").forEach(v -> formatterImplClassMap.put(v, ThreadPatternFormatter.class));

        // 文件
        Arrays.asList("F", "file").forEach(v -> formatterImplClassMap.put(v, FilePatternFormatter.class));
        // 行号
        Arrays.asList("L", "line").forEach(v -> formatterImplClassMap.put(v, LinePatternFormatter.class));

        // 内容
        Arrays.asList("m", "msg", "message").forEach(v -> formatterImplClassMap.put(v, MsgPatternFormatter.class));

        // 换行
        formatterImplClassMap.put("n", LineSeparatorPatternFormatter.class);

        // MDC
        Arrays.asList("X", "mdc", "MDC").forEach(v -> formatterImplClassMap.put(v, MdcPatternFormatter.class));
    }

    /**
     * 根据配置格式化文本来构建格式器的具体实现列表
     *
     * @param pattern 格式化文本配置
     * @return 返回格式器列表
     */
    public static List<PatternFormatter> build(String pattern) {
        return PatternParser.parseFormatterList(pattern);
    }

    /**
     * 权限解析出来的配置创建格式化对象.
     *
     * @param id             格式化配置ID
     * @param formattingInfo 格式化信息
     * @param options        格式化对象可能需要的参数
     * @return 格式化实现对象
     */
    public static PatternFormatter create(String id, FormattingInfo formattingInfo, String options) {
        Class<?> klass = formatterImplClassMap.get(id);
        if (klass == null) {
            return new LiteralPatternFormatter(("[%" + id + "(unrealized)]").toCharArray());
        }

        try {
            Constructor<?> constructor = klass.getConstructor(FormattingInfo.class, String.class);
            return (PatternFormatter) constructor.newInstance(formattingInfo, options);
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
                 InvocationTargetException e) {
            return new LiteralPatternFormatter(("[%" + id + "(" + e.getMessage() + ")]").toCharArray());
        }
    }
}