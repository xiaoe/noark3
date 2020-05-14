package xyz.noark.core.lang;

import org.junit.Test;
import xyz.noark.core.util.ThreadUtils;

/**
 * @author 小流氓[176543888@qq.com]
 * @since 3.4
 */
public class StopWatchTest {

    @Test
    public void test() {
        StopWatch watch = new StopWatch(true);
        watch.start("1");
        ThreadUtils.sleep(1);
        watch.stop();
        watch.start("2");
        ThreadUtils.sleep(2);
        watch.stop();

        System.out.println(watch.prettyPrint());
    }
}
