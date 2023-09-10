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

import java.util.AbstractList;
import java.util.List;

/**
 * 坐标点的列表.
 * <p>策划最近要配置一堆点来随机，那还是要个列表功能</p>
 *
 * @author 小流氓[176543888@qq.com]
 */
public class PointList extends AbstractList<Point> {
    private final List<Point> pointList;

    public PointList(List<Point> pointList) {
        this.pointList = pointList;
    }

    @Override
    public Point set(int index, Point element) {
        return pointList.set(index, element);
    }

    @Override
    public Point get(int index) {
        return pointList.get(index);
    }

    @Override
    public int size() {
        return pointList.size();
    }

    /**
     * 从本列表中随机生成不重复的num个点.
     * <p>如果列表数量不足时会返回全列表并不会报错</p>
     *
     * @param num 随机生成的数量
     * @return 指定数量的点列表
     */
    public List<Point> random(int num) {
        return RandomUtils.randomList(this, num);
    }

    @Override
    public String toString() {
        return "PointList" + pointList;
    }
}
