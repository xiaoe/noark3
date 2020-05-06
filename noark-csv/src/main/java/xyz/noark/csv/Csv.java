/*
 * Copyright © 2018 www.noark.xyz All Rights Reserved.
 *
 * 感谢您选择Noark框架，希望我们的努力能为您提供一个简单、易用、稳定的服务器端框架 ！
 * 除非符合Noark许可协议，否则不得使用该文件，您可以下载许可协议文件：
 *
 * 		http://www.noark.xyz/LICENSE
 *
 * 1.未经许可，任何公司及个人不得以任何方式或理由对本框架进行修改、使用和传播;
 * 2.禁止在本项目或任何子项目的基础上发展任何派生版本、修改版本或第三方版本;
 * 3.无论你对源代码做出任何修改和改进，版权都归Noark研发团队所有，我们保留所有权利;
 * 4.凡侵犯Noark版权等知识产权的，必依法追究其法律责任，特此郑重法律声明！
 */
package xyz.noark.csv;

import xyz.noark.core.annotation.tpl.TplAttr;
import xyz.noark.core.annotation.tpl.TplAttrSuffix;
import xyz.noark.core.annotation.tpl.TplFile;
import xyz.noark.core.converter.ConvertManager;
import xyz.noark.core.converter.Converter;
import xyz.noark.core.exception.ConvertException;
import xyz.noark.core.exception.TplAttrRequiredException;
import xyz.noark.core.exception.TplConfigurationException;
import xyz.noark.core.exception.UnrealizedException;
import xyz.noark.core.lang.ResourceLoader;
import xyz.noark.core.util.*;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static xyz.noark.log.LogHelper.logger;

/**
 * CSV文件解析器.
 * <p>
 * 这不是一个常规的CSV文件噢，他的分隔符长得好帅，由导表工具来维护他的唯一.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.0
 */
public class Csv extends ResourceLoader {
    private final ConvertManager convertManager = ConvertManager.getInstance();
    private final char separator;

    /**
     * 默认的构造函数是英文逗号作为分隔符.
     */
    public Csv() {
        this(',');
    }

    /**
     * 指定分隔符。
     *
     * @param separator 分隔符
     */
    public Csv(char separator) {
        this.separator = separator;
    }

    /**
     * 根据指定类文件加载CSV格式的模板.
     *
     * @param <T>          要转化对象的类型
     * @param templatePath 模板文件路径
     * @param klass        模板类文件
     * @return 模板类对象的集合
     */
    public <T> List<T> loadAll(String templatePath, Class<T> klass) {
        return loadAll(templatePath, StringUtils.EMPTY, klass);
    }

    /**
     * 根据指定类文件加载CSV格式的模板.
     *
     * @param <T>          要转化对象的类型
     * @param templatePath 模板文件路径
     * @param zone         版本编号
     * @param klass        模板类文件
     * @return 模板类对象的集合
     */
    public <T> List<T> loadAll(String templatePath, String zone, Class<T> klass) {
        TplFile file = klass.getAnnotation(TplFile.class);
        if (file == null) {
            throw new TplConfigurationException("这不是CSV格式的配置文件类:" + klass.getName());
        }

        // 如果模板类存在Set方法，给一个警告提示...
        if (MethodUtils.existSetMethod(klass)) {
            logger.warn("模板类正常为只读模式，不应该存在Set方法噢，class={}", klass.getName());
        }

        try (CsvReader reader = new CsvReader(separator, newBufferedReader(templatePath, zone, file.value(), CharsetUtils.CHARSET_UTF_8))) {
            /** 标题 */
            Map<String, Integer> titles = reader.getHeaders();

            List<T> result = new ArrayList<>();
            reader.getDatas().forEach(v -> result.add(analysisLine(klass, file.value(), titles, v)));
            return result;
        } catch (IOException e) {
            throw new TplConfigurationException("CSV格式的配置文件类:" + klass.getName(), e);
        }
    }

    private <T> T analysisLine(Class<T> klass, String tplFileName, Map<String, Integer> titles, String[] values) {
        T result = ClassUtils.newInstance(klass);
        /** 使用工具获取，父类的属性也要判定 */
        for (Field field : FieldUtils.getAllField(klass)) {
            TplAttr[] array = field.getAnnotationsByType(TplAttr.class);
            if (array == null || array.length == 0) {
                continue;
            }

            // 后缀配置
            TplAttrSuffix suffix = field.getAnnotation(TplAttrSuffix.class);

            // 只有一个配置且没有后缀配置
            if (array.length == 1 && suffix == null) {
                final TplAttr attr = array[0];

                // 查找配置
                int index = this.lookupIndex(klass, field, titles, values, attr, attr.name());
                if (index == -1) {
                    continue;
                }

                final String value = values[index];
                Converter<?> converter = this.getConverter(field);
                try {
                    FieldUtils.writeField(result, field, converter.convert(field, value));
                } catch (Exception e) {
                    throw new ConvertException(tplFileName + " >> " + field.getName() + " >> " + value + "-->" + converter.buildErrorMsg(), e);
                }
            }
            // 只有一个配置且有后缀，那也是个多个配置
            else if (array.length == 1 && suffix != null) {
                // 如果后缀配置小于等于0，那就没得玩了哈...
                if (suffix.step() <= 0) {
                    throw new ConvertException(klass.getName() + " >> " + field.getName() + " >> TplAttrSuffix#step=" + suffix.step() + "-->" + "不可以小于等于0.");
                }

                TplAttr attr = array[0];
                Map<String, String> data = new LinkedHashMap<>((suffix.end() - suffix.start()) / suffix.step() + 1);
                for (int i = suffix.start(); i <= suffix.end(); i += suffix.step()) {
                    final String attrName = attr.name() + i;

                    // 查找配置
                    int index = this.lookupIndex(klass, field, titles, values, attr, attrName);
                    if (index != -1) {
                        data.put(attrName, values[index]);
                    }
                }
                Converter<?> converter = this.getConverter(field);
                try {
                    FieldUtils.writeField(result, field, converter.convert(field, data));
                } catch (Exception e) {
                    throw new ConvertException(tplFileName + " >> " + field.getName() + " >> " + data + "-->" + converter.buildErrorMsg(), e);
                }
            }
            // 多个配置时，忽略后缀的
            else {
                Map<String, String> data = new LinkedHashMap<>(array.length + 1);
                for (TplAttr attr : array) {
                    // 查找配置
                    int index = this.lookupIndex(klass, field, titles, values, attr, attr.name());
                    if (index != -1) {
                        data.put(attr.name(), values[index]);
                    }
                }
                Converter<?> converter = this.getConverter(field);
                try {
                    FieldUtils.writeField(result, field, converter.convert(field, data));
                } catch (Exception e) {
                    throw new ConvertException(tplFileName + " >> " + field.getName() + " >> " + data + "-->" + converter.buildErrorMsg(), e);
                }
            }
        }

        return result;
    }

    private int lookupIndex(Class<?> klass, Field field, Map<String, Integer> titles, String[] values, TplAttr attr, String attrName) {
        // 找到属性名称对应值所对应的下标位置
        Integer index = titles.get(attrName);
        if (index == null) {
            if (attr.required()) {
                throw new TplAttrRequiredException(klass, field, attr);
            }
            return -1;
        }

        // 竟然有的数量不到最后
        if (index > values.length - 1) {
            return -1;
        }

        // 未配值
        String value = values[index];
        if (StringUtils.isEmpty(value)) {
            return -1;
        }

        // 其他情况都算有值
        return index.intValue();
    }

    private Converter<?> getConverter(Field field) {
        Converter<?> result = convertManager.getConverter(field.getType());
        if (result == null) {
            throw new UnrealizedException("CSV配置解析时，发现未实现的类型. field=(" + field.getType().getName() + ")" + field.getName());
        }
        return result;
    }
}