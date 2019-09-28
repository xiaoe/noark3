package xyz.noark.network;

import static org.junit.Assert.assertEquals;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Test;

public class IpManagerTest {

  @Test
  public void activityTest() {
    IpManager manager = new IpManager();
    OldIpManger oldManger = new OldIpManger();
    for (int i = 0; i < 100; i++) {
      String ip = String.valueOf(i);
      assertEquals(oldManger.active(ip), manager.active(ip));
    }
  }

  @Test
  public void inActivityTest() {
    IpManager manager = new IpManager();
    OldIpManger oldManger = new OldIpManger();
    for (int i = 0; i < 100; i++) {//先激活
      String ip = String.valueOf(i);
      assertEquals(oldManger.active(ip), manager.active(ip));
    }

    for (int i = 0; i < 100; i++) {//取消激活
      String ip = String.valueOf(i);
      oldManger.inactive(ip);
      manager.inactive(ip);
    }

    for (int i = 0; i < 100; i++) {//再次激活
      String ip = String.valueOf(i);
      assertEquals(oldManger.active(ip), manager.active(ip));
    }
  }


  public static class OldIpManger {

    /** IP统计计数 */
    private static final Map<String, AtomicInteger> COUNTS = new ConcurrentHashMap<>();

    /**
     * 新激活一个通道
     *
     * @param ip 目标IP
     * @return 返回这个IP已激活的IP数量
     */
    public int active(String ip) {
      return COUNTS.computeIfAbsent(ip, key -> new AtomicInteger(0)).incrementAndGet();
    }

    /**
     * 断开链接.
     * <p>
     * 释放这个IP计数
     *
     * @param ip 目标IP
     */
    public void inactive(String ip) {
      COUNTS.compute(ip, (k, v) -> v == null ? null : (v.decrementAndGet() <= 0) ? null : v);
    }
  }

}
