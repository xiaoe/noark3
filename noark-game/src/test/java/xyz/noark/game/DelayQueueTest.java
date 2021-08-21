package xyz.noark.game;

import com.company.game.event.BuildingUpgradeEvent;
import org.junit.Test;
import xyz.noark.core.event.DelayEvent;
import xyz.noark.core.util.DateUtils;

import java.util.Date;
import java.util.concurrent.DelayQueue;

/**
 * 延迟队列的测试用例
 *
 * @author 小流氓[176543888@qq.com]
 */
public class DelayQueueTest {

    @Test
    public void test() {
        DelayQueue<DelayEvent> queue = new DelayQueue<>();
        BuildingUpgradeEvent event;
        {
            event = new BuildingUpgradeEvent();
            event.setId(1);
            event.setMsg("第1个事件");
            event.setEndTime(new Date());
            queue.add(event);
        }
        {
            event = new BuildingUpgradeEvent();
            event.setId(1);
            event.setMsg("第2个事件");
            event.setEndTime(DateUtils.addDays(new Date(), -2));
            queue.add(event);
        }
        {
            event = new BuildingUpgradeEvent();
            event.setId(1);
            event.setMsg("第3个事件");
            event.setEndTime(DateUtils.addDays(new Date(), -1));
            queue.add(event);
        }
        assert queue.size() == 3;

        if (queue.remove(event)) {
            assert queue.size() == 2;
        }

        BuildingUpgradeEvent finalEvent = event;
        queue.removeIf(v -> v.equals(finalEvent));
        assert queue.size() == 0;
    }
}
