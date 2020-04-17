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

import xyz.noark.core.env.EnvConfigHolder;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 一个对象数据.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.1
 */
class ObjectData {
    private final List<String> nodePaths = new LinkedList<>();
    private final Map<String, String> data = new HashMap<>(64);

    String getValue(String key) {
        return data.get(key);
    }

    /**
     * 添加当前处理的节点.
     *
     * @param qName 节点名称.
     */
    void addHandleNode(String qName) {
        nodePaths.add(qName);
    }

    /**
     * 移除当前处理的节点
     *
     * @param qName 节点名称.
     */
    void removeHandleNode(String qName) {
        // 移除最后一个节点
        nodePaths.remove(qName);
    }

    /**
     * 存入数据.
     *
     * @param qName 属性名称
     * @param value 属性值
     */
    void putAttrData(String qName, String value) {
        StringBuilder sb = new StringBuilder(128);
        nodePaths.forEach((v) -> sb.append(v).append('.'));
        data.put(sb.append(qName).toString(), value);
    }

    /**
     * 填充EL表达式.
     */
    public void fillExpression() {
        for (Map.Entry<String, String> e : data.entrySet()) {
            e.setValue(EnvConfigHolder.fillExpression(e.getValue(), data, true));
        }
    }
}