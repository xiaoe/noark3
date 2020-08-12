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
package xyz.noark.game.template;

import xyz.noark.core.annotation.Autowired;
import xyz.noark.core.exception.TemplateNotFoundException;
import xyz.noark.core.lang.PairMap;
import xyz.noark.core.lang.TripleMap;

import java.io.Serializable;
import java.util.Map;
import java.util.Optional;

/**
 * 模板管理器.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.0
 */
public abstract class AbstractTemplateManager {

    @Autowired
    protected TemplateLoader templateLoader;

    /**
     * 获取当前模块名称，用于输入日志.
     *
     * @return 模块名称
     */
    public abstract String getModuleName();

    /**
     * 加载模板数据的过程.
     */
    public abstract void loadData();

    /**
     * 所有模板都加载完成后，进行的数据校验.
     * <p>
     * 此逻辑只有在Debug模式时才会被调用
     */
    public void checkValidity() {
    }

    /**
     * 提供一种直接获取模板的方案.
     * <p>
     * 如果没有找到模板那就要抛出一个异常{@link TemplateNotFoundException}
     *
     * @param <T>       模板的类型
     * @param <K>       集合中的Key的类型
     * @param klass     模板的类型
     * @param templates 存储模板对象的集合
     * @param key       模板对象所在集合中的Key
     * @return 如果存储指定Key的模板则返回模板对象，否则会抛出一个异常
     */
    protected <T, K extends Serializable> T getTemplateOrElseThrow(Class<T> klass, Map<K, T> templates, K key) {
        return Optional.ofNullable(templates.get(key)).orElseThrow(() -> new TemplateNotFoundException(klass, key));
    }

    /**
     * 提供一种直接获取模板的方案.
     * <p>
     * 如果没有找到模板那就要抛出一个异常{@link TemplateNotFoundException}
     *
     * @param <T>       模板的类型
     * @param <L>       键之左边元素类型
     * @param <R>       键之右边元素类型
     * @param klass     模板的类型
     * @param templates 存储模板对象的集合
     * @param left      键之左边元素
     * @param right     键之右边元素
     * @return 如果存储指定Key的模板则返回模板对象，否则会抛出一个异常
     */
    protected <T, L extends Serializable, R extends Serializable> T getTemplateOrElseThrow(Class<T> klass, PairMap<L, R, T> templates, L left, R right) {
        return Optional.ofNullable(templates.get(left, right)).orElseThrow(() -> new TemplateNotFoundException(klass, left, right));
    }

    /**
     * 提供一种直接获取模板的方案.
     * <p>
     * 如果没有找到模板那就要抛出一个异常{@link TemplateNotFoundException}
     *
     * @param <T>       模板的类型
     * @param <L>       键之左边元素类型
     * @param <M>       键之中间元素类型
     * @param <R>       键之右边元素类型
     * @param klass     模板的类型
     * @param templates 存储模板对象的集合
     * @param left      键之左边元素
     * @param middle    键之中间元素
     * @param right     键之右边元素
     * @return 如果存储指定Key的模板则返回模板对象，否则会抛出一个异常
     */
    protected <T, L extends Serializable, M extends Serializable, R extends Serializable> T getTemplateOrElseThrow(Class<T> klass, TripleMap<L, M, R, T> templates, L left, M middle, R right) {
        return Optional.ofNullable(templates.get(left, middle, right)).orElseThrow(() -> new TemplateNotFoundException(klass, left, middle, right));
    }
}