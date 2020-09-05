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
package xyz.noark.xml;

import org.xml.sax.helpers.DefaultHandler;
import xyz.noark.core.annotation.tpl.TplAttr;
import xyz.noark.core.converter.ConvertManager;
import xyz.noark.core.converter.Converter;
import xyz.noark.core.exception.ConvertException;
import xyz.noark.core.exception.TplAttrRequiredException;
import xyz.noark.core.util.ClassUtils;
import xyz.noark.core.util.FieldUtils;
import xyz.noark.core.util.StringUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * 抽象的XML处理器.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.1
 */
abstract class AbstractXmlHandler<T> extends DefaultHandler {
    protected final Class<T> klass;
    protected final String tplFileName;

    protected AbstractXmlHandler(Class<T> klass, String tplFileName) {
        this.klass = klass;
        this.tplFileName = tplFileName;
    }

    /**
     * 构建对象.
     *
     * @param root  XML数据
     * @param fixEl 是否修正EL表达式
     */
    protected T buildObject(XmlNode root, boolean fixEl) {
        // 修补参数引用
        if (fixEl) {
            root.fillExpression();
        }
        return (T) this.buildObject(klass, root);
    }

    /**
     * 构建List属性对象.
     *
     * @param node  XML节点
     * @param attr  属性配置
     * @param field 属性反射对象
     * @return List属性对象
     */
    private List<Object> buildListFieldObject(XmlNode node, TplAttr attr, Field field) {
        List<XmlNode> nodeList = node.getNodeList(attr.name());
        if (nodeList.isEmpty()) {
            if (attr.required()) {
                throw new TplAttrRequiredException(klass, field, attr);
            } else {
                return new ArrayList<>();
            }
        } else {
            // 取出这个List对应的泛型Class
            Class<?> keyClass = FieldUtils.getMapFieldKeyClass(field);
            List<Object> result = new ArrayList<>(nodeList.size());
            for (XmlNode xmlNode : nodeList) {
                result.add(buildObject(keyClass, xmlNode));
            }
            return result;
        }
    }

    private Object buildObject(Class<?> klass, XmlNode node) {
        Object result = ClassUtils.newInstance(klass);
        for (Field field : klass.getDeclaredFields()) {
            TplAttr attr = field.getAnnotation(TplAttr.class);
            if (attr == null || StringUtils.isEmpty(attr.name())) {
                continue;
            }

            // 查询转化器如果存在则这个可以使用转化器来完成.
            Converter<?> converter = this.getConverter(field);
            if (converter != null) {
                FieldUtils.writeField(result, field, this.buildConverterObject(converter, attr, node, field));
            }
            // List类型的属性
            else if (field.getType().isAssignableFrom(List.class)) {
                FieldUtils.writeField(result, field, this.buildListFieldObject(node, attr, field));
            }
            // 处理常规属性
            else {
                FieldUtils.writeField(result, field, this.buildObject(klass, node));
            }
        }
        return result;


    }

    private Object buildConverterObject(Converter<?> converter, TplAttr attr, XmlNode node, Field field) {
        String value = node.getAttributesValue(attr.name());
        if (value == null) {
            if (attr.required()) {
                throw new TplAttrRequiredException(klass, field, attr);
            }
        }
        try {
            return converter.convert(field, value);
        } catch (Exception e) {
            throw new ConvertException(tplFileName + " >> " + field.getName() + " >> " + value + "-->" + converter.buildErrorMsg(), e);
        }
    }

    private Converter<?> getConverter(Field field) {
        return ConvertManager.getInstance().getConverter(field.getType());
    }
}