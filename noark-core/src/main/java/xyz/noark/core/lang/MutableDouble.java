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
 * 一种可变double类型的实现.
 * <p>
 * 部分方法实现直接调用(JDK8)Double类中的静态方法
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.2
 */
public class MutableDouble extends Number implements Comparable<MutableDouble>, Mutable<Number> {
    private static final long serialVersionUID = -8771778938501730509L;
    private double value;

    public MutableDouble() {
    }

    public MutableDouble(double value) {
        this.value = value;
    }

    public MutableDouble(Number value) {
        this(value.doubleValue());
    }

    public MutableDouble(String value) {
        this.value = Double.parseDouble(value);
    }

    @Override
    public Double getValue() {
        return Double.valueOf(value);
    }

    @Override
    public void setValue(Number value) {
        this.value = value.doubleValue();
    }

    /**
     * 设置值，在直接使用此类时，可以不用进行装箱操作
     *
     * @param value 值
     */
    public void setValue(double value) {
        this.value = value;
    }

    @Override
    public int compareTo(MutableDouble other) {
        return Double.compare(value, other.value);
    }

    @Override
    public int intValue() {
        return (int) value;
    }

    @Override
    public long longValue() {
        return (long) value;
    }

    @Override
    public float floatValue() {
        return (float) value;
    }

    @Override
    public double doubleValue() {
        return value;
    }

    @Override
    public int hashCode() {
        return Double.hashCode(value);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof MutableDouble) {
            return value == ((MutableDouble) obj).doubleValue();
        }
        return false;
    }

    @Override
    public String toString() {
        return Double.toString(value);
    }

    /**
     * 获得当前值后进行加一操作.
     * <p>
     * i++
     *
     * @return 获得当前值后进行加一操作
     */
    public final double getAndIncrement() {
        final double oldValue = value;
        this.value++;
        return oldValue;
    }

    /**
     * 获得当前值后进行减一操作.
     * <p>
     * i--
     *
     * @return 获得当前值后进行减一操作
     */
    public final double getAndDecrement() {
        final double oldValue = value;
        this.value--;
        return oldValue;
    }

    /**
     * 获得当前值后进行加法操作.
     *
     * @param delta 要加的值
     * @return 获得当前值后进行加法操作
     */
    public final double getAndAdd(double delta) {
        final double oldValue = value;
        this.value += delta;
        return oldValue;
    }

    /**
     * 先加一再获取.
     * <p>
     * ++i
     *
     * @return 先加一再获取
     */
    public final double incrementAndGet() {
        return ++value;
    }

    /**
     * 先减一再获取.
     * <p>
     * --i
     *
     * @return 先减一再获取
     */
    public final double decrementAndGet() {
        return --value;
    }

    /**
     * 加上指定值后再返回.
     *
     * @param delta 指定值
     * @return 加上指定值后再返回
     */
    public final double addAndGet(double delta) {
        return value += delta;
    }
}