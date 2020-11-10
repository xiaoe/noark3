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
