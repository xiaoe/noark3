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
package xyz.noark.core.lang;

/**
 * 一种可以直接存储原生float类型的有序的列表接口.
 * <p>
 * 目前支持功能有限，需要努力补一下
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.0
 */
public interface FloatList {

    // Query Operations

    /**
     * 返回列表中的元素个数。
     *
     * @return 列表中的元素个数.
     */
    int size();

    /**
     * 如果此列表中没有元素，则返回 true
     *
     * @return 如果此列表中没有元素，则返回 true
     */
    boolean isEmpty();

    /**
     * 如果此列表中包含指定的元素，则返回 true。
     * <p>
     * 更确切地讲，当且仅当此列表包含至少一个满足 (o==e) 的元素 e 时，则返回 true。
     *
     * @param o 测试此列表中是否存在的元素
     * @return 如果此列表包含特定的元素，则返回 true
     */
    boolean contains(float o);

    /**
     * 按适当顺序（从第一个到最后一个元素）返回包含此列表中所有元素的数组。
     * <p>
     * 由于此列表不维护对返回数组的任何引用，，因而它将是“安全的”。（换句话说，此方法必须分配一个新的数组）<br>
     * 因此，调用者可以自由地修改返回的数组。
     *
     * @return 包含此列表中所有元素的数组（按适当顺序）
     */
    float[] toArray();

    /**
     * 从列表中随机出一个元素.
     *
     * @return 随机元素
     */
    float random();

    // Modification Operations

    /**
     * 将指定的元素添加到此列表的尾部。
     *
     * @param o 要添加到此列表中的元素
     * @return 如果此 collection 由于调用而发生更改，则返回 true
     */
    boolean add(float o);

    /**
     * 移除此列表中首次出现的指定元素（如果存在）。
     * <p>
     * 如果列表不包含此元素，则列表不做改动。更确切地讲，移除满足 (o==e) 的最低索引的元素（如果存在此类元素）。<br>
     * 如果列表中包含指定的元素，则返回 true（或者等同于这种情况：如果列表由于调用而发生更改，则返回 true）。
     *
     * @param o 要从此列表中移除的元素（如果存在）
     * @return 如果此列表包含指定的元素，则返回 true
     */
    boolean remove(float o);

    /**
     * 获取第几个位置的数值
     *
     * @param index 位置（数组下标）
     * @return 数值
     */
    float get(int index);

    // Bulk Operations

    /**
     * 移除此列表中的所有元素。此调用返回后，列表将为空。
     */
    void clear();
}
