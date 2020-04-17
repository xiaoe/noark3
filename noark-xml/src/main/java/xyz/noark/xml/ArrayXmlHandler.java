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
package xyz.noark.xml;

import org.xml.sax.Attributes;

import java.util.ArrayList;
import java.util.List;

/**
 * 用SAX解析XML的Handler
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.1
 */
class ArrayXmlHandler<T> extends AbstractXmlHandler<T> {
    private static final String OBJECT_NODE = "object";
    private final List<ObjectData> dataList = new ArrayList<>(1024);

    public ArrayXmlHandler(Class<T> klass, String tplFileName) {
        super(klass, tplFileName);
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        // 只处理Object节点
        if (OBJECT_NODE.equalsIgnoreCase(qName)) {
            ObjectData data = new ObjectData();
            for (int i = 0, len = attributes.getLength(); i < len; i++) {
                data.putAttrData(attributes.getQName(i), attributes.getValue(i));
            }
            dataList.add(data);
        }
    }

    public List<T> getResult() {
        List<T> result = new ArrayList<>(dataList.size());
        dataList.forEach(v -> result.add(buildObject(v, false)));
        return result;
    }
}