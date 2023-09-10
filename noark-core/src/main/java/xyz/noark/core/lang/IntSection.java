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
package xyz.noark.core.lang;

import xyz.noark.core.util.RandomUtils;

/**
 * Int数字区间.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.4.3
 */
public class IntSection {
    private final int min;
    private final int max;

    public IntSection(int value) {
        this(value, value);
    }

    public IntSection(int min, int max) {
        this.min = min;
        this.max = max;
    }

    /**
     * 判定一个数字是不是在这个区间之内，此区间两端都为闭区间
     *
     * @param element 判定的那个数字
     * @return 如果在此区间内，则返回true
     */
    public boolean contains(int element) {
        return min <= element && element <= max;
    }

    /**
     * 在区间中随机出一个数字
     *
     * @return 随机数字
     */
    public int random() {
        // 如果相等，直接返回
        if (min == max) {
            return min;
        }
        // 有区间存在，则进行随机
        return RandomUtils.nextInt(min, max + 1);
    }

    @Override
    public String toString() {
        return "IntSection [min=" + min + ", max=" + max + "]";
    }
}