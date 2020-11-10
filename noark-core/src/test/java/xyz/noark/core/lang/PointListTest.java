package xyz.noark.core.lang;

import org.junit.Test;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * 坐标列表测试用例.
 *
 * @author 小流氓[176543888@qq.com]
 */
public class PointListTest {
    @Test
    public void testIsValid() {
        PointList list = new PointList(Arrays.asList(Point.valueOf(1,2),Point.valueOf(3,4)));
        System.out.println(list);
        System.out.println(list.random(2));
    }
}
