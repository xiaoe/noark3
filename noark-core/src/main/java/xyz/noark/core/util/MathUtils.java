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
package xyz.noark.core.util;

import xyz.noark.core.lang.Point;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 数学计算相关的工具类库.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.2
 */
public class MathUtils {
    /**
     * 一
     */
    public static final double ONE = 1.0;
    /**
     * 百
     */
    public static final double HUNDRED = 100.0;
    /**
     * 千
     */
    public static final double THOUSAND = 1000.0;
    /**
     * 万
     */
    public static final double TEN_THOUSAND = 1_0000.0;
    /**
     * 百万
     */
    public static final double MILLION = 100_0000.0;

    /**
     * 计算两个参数的和，如果相加出现溢出那就返回{@code int}的最大值.
     * <p>
     * 区别于JDK的方法，仅仅认同判定方案，游戏世界，溢出时那就修正一个合理的值，一般调用此方法的游戏逻辑决不能因异常而中断
     *
     * @param x 第一个参数
     * @param y 第二个参数
     * @return 两个参数的和
     * @see Math#addExact(int, int)
     */
    public static int addExact(int x, int y) {
        try {
            return Math.addExact(x, y);
        } catch (ArithmeticException e) {
            return Integer.MAX_VALUE;
        }
    }

    /**
     * 计算两个参数的和，如果相加出现溢出那就返回{@code long}的最大值.
     * <p>
     * 区别于JDK的方法，仅仅认同判定方案，游戏世界，溢出时那就修正一个合理的值，一般调用此方法的游戏逻辑决不能因异常而中断
     *
     * @param x 第一个参数
     * @param y 第二个参数
     * @return 两个参数的和
     * @see Math#addExact(long, long)
     */
    public static long addExact(long x, long y) {
        try {
            return Math.addExact(x, y);
        } catch (ArithmeticException e) {
            return Long.MAX_VALUE;
        }
    }

    /**
     * 计算两个参数的乘积，如果相乘出现溢出那就返回{@code int}的最大值.
     * <p>
     * 区别于JDK的方法，仅仅认同判定方案，游戏世界，溢出时那就修正一个合理的值，一般调用此方法的游戏逻辑决不能因异常而中断
     *
     * @param x 第一个参数
     * @param y 第二个参数
     * @return 两个参数的乘积
     * @see Math#multiplyExact(int, int)
     */
    public static int multiplyExact(int x, int y) {
        try {
            return Math.multiplyExact(x, y);
        } catch (ArithmeticException e) {
            return Integer.MAX_VALUE;
        }
    }

    /**
     * 计算两个参数的乘积，如果相乘出现溢出那就返回{@code long}的最大值.
     * <p>
     * 区别于JDK的方法，仅仅认同判定方案，游戏世界，溢出时那就修正一个合理的值，一般调用此方法的游戏逻辑决不能因异常而中断
     *
     * @param x 第一个参数
     * @param y 第二个参数
     * @return 两个参数的乘积
     * @see Math#multiplyExact(long, long)
     */
    public static long multiplyExact(long x, long y) {
        try {
            return Math.multiplyExact(x, y);
        } catch (ArithmeticException e) {
            return Long.MAX_VALUE;
        }
    }

    /**
     * 计算两点(x1,y1)到(x2,y2)的距离.
     * <p>
     * Math.sqrt(|x1-x2|² + |y1-y2|²)
     *
     * @param x1 坐标X1
     * @param y1 坐标Y1
     * @param x2 坐标X2
     * @param y2 坐标Y2
     * @return 两点的距离
     */
    public static double distance(int x1, int y1, int x2, int y2) {
        final double x = Math.abs(x1 - x2);
        final double y = Math.abs(y1 - y2);
        return Math.sqrt(x * x + y * y);
    }

    /**
     * 计算两点(x1,y1)到(x2,y2)的距离.
     * <p>
     * Math.sqrt(|x1-x2|² + |y1-y2|²)
     *
     * @param x1 坐标X1
     * @param y1 坐标Y1
     * @param x2 坐标X2
     * @param y2 坐标Y2
     * @return 两点的距离
     */
    public static double distance(double x1, double y1, double x2, double y2) {
        final double x = Math.abs(x1 - x2);
        final double y = Math.abs(y1 - y2);
        return Math.sqrt(x * x + y * y);
    }

    /**
     * 计算两点P1(x1,y1)到P2(x2,y2)的距离.
     * <p>
     * Math.sqrt(|x1-x2|² + |y1-y2|²)
     *
     * @param p1 坐标1
     * @param p2 坐标2
     * @return 两点的距离
     */
    public static double distance(Point p1, Point p2) {
        return distance(p1.getX(), p1.getY(), p2.getX(), p2.getY());
    }

    /**
     * 判定两点(x1,y1)和(x2,y2)是否相邻.
     * <p>
     * 可用于两个AOI是否相邻判定
     *
     * @param x1 坐标X1
     * @param y1 坐标Y1
     * @param x2 坐标X2
     * @param y2 坐标Y2
     * @return 如果两坐标相邻返回true, 否则返回false
     */
    public static boolean adjacent(int x1, int y1, int x2, int y2) {
        return Math.abs(x1 - x2) <= 1 && Math.abs(y1 - y2) <= 1;
    }

    /**
     * 判定两点P1(x1,y1)和P2(x2,y2)是否相邻.
     * <p>
     * 可用于两个AOI是否相邻判定
     *
     * @param p1 坐标1
     * @param p2 坐标2
     * @return 如果两坐标相邻返回true, 否则返回false
     */
    public static boolean adjacent(Point p1, Point p2) {
        return adjacent(p1.getX(), p1.getY(), p2.getX(), p2.getY());
    }

    /**
     * 向下取整，并返回int值.
     *
     * @param a 一个带有小数的数值
     * @return 返回向下取整后的int值
     */
    public static int floorInt(double a) {
        return (int) Math.floor(a);
    }

    /**
     * 向下取整，并返回long值.
     *
     * @param a 一个带有小数的数值
     * @return 返回向下取整后的long值
     */
    public static long floorLong(double a) {
        return (long) Math.floor(a);
    }

    /**
     * 向上取整，并返回int值.
     *
     * @param a 一个带有小数的数值
     * @return 返回向上取整后的int值
     */
    public static int ceilInt(double a) {
        return (int) Math.ceil(a);
    }

    /**
     * 向上取整，并返回long值.
     *
     * @param a 一个带有小数的数值
     * @return 返回向上取整后的long值
     */
    public static long ceilLong(double a) {
        return (long) Math.ceil(a);
    }

    /**
     * 4舍5入取整，并返回int值.
     *
     * @param a 一个带有小数的数值
     * @return 返回向上取整后的int值
     */
    public static int roundInt(double a) {
        return (int) Math.round(a);
    }

    /**
     * 4舍5入取整，并返回long值.
     *
     * @param a 一个带有小数的数值
     * @return 返回向上取整后的long值
     */
    public static long roundLong(double a) {
        return (long) Math.round(a);
    }

    /**
     * 格式化小数位数的方法.
     * <p>
     * 采用了{@link BigDecimal#setScale(int, RoundingMode)}方式来保留小数位数<br>
     * 默认舍入方式为4舍5入, 参考{@link RoundingMode#HALF_UP}
     *
     * @param value    原始值
     * @param newScale 保留小数位数
     * @return 返回要被保留指定小数位数的值.
     */
    public static float formatScale(float value, int newScale) {
        return formatScale(value, newScale, RoundingMode.HALF_UP);
    }

    /**
     * 格式化小数位数的方法.
     * <p>
     * 采用了{@link BigDecimal#setScale(int, RoundingMode)}方式来保留小数位数
     *
     * @param value    原始值
     * @param newScale 保留小数位数
     * @param mode     被保留位数后舍入方式，参考{@link RoundingMode}
     * @return 返回要被保留指定小数位数的值.
     */
    public static float formatScale(float value, int newScale, RoundingMode mode) {
        return BigDecimal.valueOf(value).setScale(newScale, mode).floatValue();
    }

    /**
     * 格式化小数位数的方法.
     * <p>
     * 采用了{@link BigDecimal#setScale(int, RoundingMode)}方式来保留小数位数<br>
     * 默认舍入方式为4舍5入, 参考{@link RoundingMode#HALF_UP}
     *
     * @param value    原始值
     * @param newScale 保留小数位数
     * @return 返回要被保留指定小数位数的值.
     */
    public static double formatScale(double value, int newScale) {
        return formatScale(value, newScale, RoundingMode.HALF_UP);
    }

    /**
     * 格式化小数位数的方法.
     * <p>
     * 采用了{@link BigDecimal#setScale(int, RoundingMode)}方式来保留小数位数
     *
     * @param value    原始值
     * @param newScale 保留小数位数
     * @param mode     被保留位数后舍入方式，参考{@link RoundingMode}
     * @return 返回要被保留指定小数位数的值.
     */
    public static double formatScale(double value, int newScale, RoundingMode mode) {
        return BigDecimal.valueOf(value).setScale(newScale, mode).doubleValue();
    }

    /**
     * N种资源掠夺最优计算方案.
     * <p>
     * 有N种资源，尝试抢其他的部分，但各种资源有一定的比例... <br>
     * 使用场景：SLG的城池掠夺资源计算
     *
     * @param <T>       资源类型
     * @param resources N种资源(参数选用LinkedHashMap，就是想按顺序优先扣前面的...)
     * @param max       掠夺的最大值
     * @param ratio     掠夺比例<b>建议：比例总和在100以内</b>
     * @return 一种最优的掠夺结果
     */
    public static <T> Map<T, Long> plunder(Map<T, Long> resources, long max, Map<T, Integer> ratio) {
        final Map<T, Long> result = MapUtils.newHashMap(ratio.size());
        if (max <= 0) {
            return result;
        }
        plunder(resources, result, max, ratio);
        return result;
    }

    private static <T> void plunder(final Map<T, Long> resources, final Map<T, Long> result, long max, Map<T, Integer> ratio) {
        // 比率总和
        int ratioSum = 0;
        for (Entry<T, Integer> e : ratio.entrySet()) {
            Long value = resources.get(e.getKey());
            if (value != null && value > 0 && e.getValue() > 0) {
                ratioSum += e.getValue();
            }
        }

        // 已掠夺的数量
        long plunderNum = 0;
        // 资源一种一种的处理
        for (Entry<T, Long> e : resources.entrySet()) {
            if (e.getValue() <= 0) {
                continue;
            }
            Integer r = ratio.get(e.getKey());
            // 这种资源没有配置比例就是不能抢
            if (r == null) {
                continue;
            }

            // 默认这个比例抢的最大值
            long ratioValue = MathUtils.floorLong(max * (r.doubleValue() / ratioSum));
            if (ratioValue <= 0) {
                continue;
            }

            // 当前资源总量
            long totalValue = e.getValue();
            // 如果资源能满足被抢的量
            if (totalValue >= ratioValue) {
                plunderNum += ratioValue;
                e.setValue(totalValue - ratioValue);
                MapUtils.addValue(result, e.getKey(), ratioValue);
            }
            // 不够，那就有多少算多少
            else {
                plunderNum += totalValue;
                e.setValue(0L);
                MapUtils.addValue(result, e.getKey(), totalValue);
            }
        }

        // 已掠夺数量为0时，那就是比例精度问题，需要手工容错处理了
        if (plunderNum <= 0) {
            fixPlunderDeficiency(resources, result, max, ratio);
        }
        // 能掠夺到资源，那就把差的再递归一次
        else if (max > plunderNum) {
            plunder(resources, result, max - plunderNum, ratio);
        }
    }

    private static <T> void fixPlunderDeficiency(final Map<T, Long> resources, final Map<T, Long> result, long max, Map<T, Integer> ratio) {
        // 资源一种一种的处理
        for (Entry<T, Long> e : resources.entrySet()) {
            if (e.getValue() <= 0) {
                continue;
            }
            Integer r = ratio.get(e.getKey());
            // 这种资源没有配置比例就是不能抢
            if (r == null || r <= 0) {
                continue;
            }
            // 当前资源总量
            long totalValue = e.getValue();
            // 如果资源能满足被抢的量
            if (totalValue >= max) {
                e.setValue(totalValue - max);
                MapUtils.addValue(result, e.getKey(), max);
                break;
            }
            // 不够，那就有多少算多少
            else {
                e.setValue(0L);
                MapUtils.addValue(result, e.getKey(), totalValue);
                max = max - totalValue;
            }
        }
    }

    /**
     * long类型的数值按比率转化为double类型的值.
     * <p>
     * 由于配置表在转化中精度丢失问题，建议策划配置的是long类型的数值，所以就有了这个转化方法。 <br>
     * 比如：约定XX列为百分比，那配置50，就是50%，等于0.5
     *
     * @param value long类型的数值
     * @param ratio 比率
     * @return double类型的值
     */
    public static double longToDouble(long value, double ratio) {
        return value / ratio;
    }

    /**
     * long类型的数值以千分比转化为double类型的值.
     * <p>
     * 参考 {@link MathUtils#longToDouble(long, double)}
     *
     * @param value long类型的数值
     * @return double类型的值
     */
    public static double permillage(long value) {
        return MathUtils.longToDouble(value, MathUtils.THOUSAND);
    }

    /**
     * long类型的数值以百分比转化为double类型的值.
     * <p>
     * 参考 {@link MathUtils#longToDouble(long, double)}
     *
     * @param value long类型的数值
     * @return double类型的值
     */
    public static double percentage(long value) {
        return MathUtils.longToDouble(value, MathUtils.HUNDRED);
    }
}