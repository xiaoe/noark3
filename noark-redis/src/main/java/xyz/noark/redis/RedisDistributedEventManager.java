package xyz.noark.redis;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import xyz.noark.core.annotation.Autowired;
import xyz.noark.core.annotation.Value;
import xyz.noark.core.event.DistributedDelayEvent;
import xyz.noark.core.event.DistributedEventManager;
import xyz.noark.core.event.EventManager;
import xyz.noark.core.thread.NamedThreadFactory;
import xyz.noark.core.util.IntUtils;
import xyz.noark.core.util.ThreadUtils;
import xyz.noark.game.NoarkConstant;

import javax.annotation.PostConstruct;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static xyz.noark.log.LogHelper.logger;

/**
 * 一种基于Redis实现的分布式事件管理器.
 *
 * @author 小流氓[176543888@qq.com]
 */
public class RedisDistributedEventManager implements DistributedEventManager {
    private static final int SHUTDOWN_MAX_TIME = 10;

    private static final String KEY_DELAY_JOB = "noark:delay:job";
    private static final String KEY_DELAY_TIMER = "noark:delay:timer";
    private static final String KEY_DELAY_QUEUE = "noark:delay:queue";

    /**
     * 脚本：发布未到期的事件
     */
    private static final String SCRIPT_PUBLISH_UNEXPIRED_EVENT = "" +
            "local val = redis.call('HSETNX', KEYS[1], ARGV[1], ARGV[3]);\n" +
            "if(val == 1) then\n" +
            "    redis.call('ZADD', KEYS[2], ARGV[2], ARGV[1]);\n" +
            "    return 1;\n" +
            "end\n" +
            "return 0";

    /**
     * 脚本：发布已到期的事件
     */
    private static final String SCRIPT_PUBLISH_EXPIRED_EVENT = "" +
            "local val = redis.call('HSETNX', KEYS[1], ARGV[1], ARGV[3]);\n" +
            "if(val == 1) then\n" +
            "    redis.call('ZADD', KEYS[2], ARGV[2], ARGV[1]);\n" +
            "    redis.call('RPUSH', KEYS[3], ARGV[3]);\n" +
            "    return 1;\n" +
            "end\n" +
            "return 0";

    /**
     * 脚本：发布已到期的事件
     */
    private static final String SCRIPT_REMOVE_EVENT = "" +
            "local val = redis.call('HDEL', KEYS[1], ARGV[1]);\n" +
            "if(val == 1) then\n" +
            "    redis.call('ZREM', KEYS[2], ARGV[1]);\n" +
            "    return 1;\n" +
            "end\n" +
            "return 0";

    /**
     * 搬运脚本
     */
    private static final String SCRIPT_CHECK_AND_TRANSPORT = "" +
            "local val = redis.call('ZRANGEBYSCORE', KEYS[1], 0, ARGV[1], 'LIMIT', 0, ARGV[2])\n" +
            "if(next(val) ~= nil) then\n" +
            "    for i = 1, #val do\n" +
            "        local job = redis.call('HGET', KEYS[3], val[i]);\n" +
            "        if(job ~= nil) then\n" +
            "            redis.call('ZINCRBY', KEYS[1], 60000, val[i]);\n" +
            "            redis.call('rpush', KEYS[2], job);\n" +
            "        else\n" +
            "            redis.call('zrem', KEYS[1], val[i]);\n" +
            "        end\n" +
            "    end\n" +
            "    return #val;\n" +
            "end\n" +
            "return 0";

    /**
     * 二次发布延迟时间，单位：毫秒，默认：1分钟
     */
    private static final int DOUBLE_PUBLISH_DELAY_TIME = 60 * 1000;

    @Autowired
    private EventManager eventManager;
    @Autowired
    private RedisTemplate redisTemplate;
    /**
     * 当前区服编号...
     */
    @Value(NoarkConstant.SERVER_ID)
    private int serverId;


    /**
     * 执行搬运与Take工作，固定两个线程
     */
    private final ScheduledExecutorService scheduledExecutor;

    public RedisDistributedEventManager() {


        this.scheduledExecutor = new ScheduledThreadPoolExecutor(2, new NamedThreadFactory("distributed_event"));
    }

    @PostConstruct
    public void init() {
        // 启动一个定时Take的任务
        this.initDelayTakeTask();
        // 启动一个任务到期触发的任务
        this.initDelayTriggerTask();
    }

    private void initDelayTriggerTask() {
        int initialDelay = (3 - LocalTime.now().getSecond() % 3) * serverId % 3;
        scheduledExecutor.scheduleWithFixedDelay(this::checkAndTransport, initialDelay, 15, TimeUnit.SECONDS);
    }

    private void initDelayTakeTask() {
        scheduledExecutor.execute(this::take);
    }

    public void take() {
        try {
            this.doTake();
        } catch (Throwable e) {
            logger.warn("延迟Take异常 {}", e);
            ThreadUtils.sleep(100);
        } finally {
            this.initDelayTakeTask();
        }
    }

    private void doTake() {
        logger.debug("Take队列，进入一小时等待期...");
        List<String> jobJsonList = redisTemplate.opsForList().brpop(60 * 60, KEY_DELAY_QUEUE);
        if (jobJsonList.size() == IntUtils.NUM_2) {
            eventManager.publish(JSON.parseObject(jobJsonList.get(1), DistributedDelayEvent.class));
        }
        // 非法情况
        else {
            logger.warn("Take到异常任务：{}", jobJsonList);
        }
    }

    @Override
    public void shutdown() {
        logger.info("开始通知分布式延迟任务工作线程池停止服务.");
        scheduledExecutor.shutdown();
        try {
            if (!scheduledExecutor.awaitTermination(SHUTDOWN_MAX_TIME, TimeUnit.SECONDS)) {
                scheduledExecutor.shutdownNow();
            }
            logger.info("处理分布式延迟任务工作线程池已停止服务");
        } catch (InterruptedException ie) {
            logger.error("停止分布式延迟任务工作线程时发生异常.", ie);
            scheduledExecutor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }


    /**
     * 搬运
     */
    public void checkAndTransport() {
        List<String> keyList = new ArrayList<>(3);
        keyList.add(KEY_DELAY_TIMER);
        keyList.add(KEY_DELAY_QUEUE);
        keyList.add(KEY_DELAY_JOB);

        List<String> valueList = new ArrayList<>(2);
        valueList.add(String.valueOf(System.currentTimeMillis()));
        valueList.add(String.valueOf(32));

        Object result = redisTemplate.eval(SCRIPT_CHECK_AND_TRANSPORT, keyList, valueList);
        logger.debug("搬运工作中，本次搬运数量={}", result);
    }

    // ----------------------------------发布事件------------------------------

    @Override
    public boolean publish(DistributedDelayEvent event) {
        final long now = System.currentTimeMillis();

        // 直接是一个已到期的任务
        if (now >= event.getEndTime().getTime()) {
            return publishExpiredEvent(event, now);
        }

        // 还没到期的任务
        return publishUnexpiredEvent(event);
    }

    /**
     * 发布一个未到期的延迟事件.
     *
     * @param event 延迟事件
     * @return 发布成功返回true
     */
    private boolean publishUnexpiredEvent(DistributedDelayEvent event) {
        List<String> keyList = new ArrayList<>(2);
        keyList.add(KEY_DELAY_JOB);
        keyList.add(KEY_DELAY_TIMER);

        List<String> valueList = new ArrayList<>(3);
        valueList.add(buildEventId(event));
        valueList.add(String.valueOf(event.getEndTime().getTime()));
        valueList.add(JSON.toJSONString(event, SerializerFeature.WriteClassName));

        return "1".equals(redisTemplate.eval(SCRIPT_PUBLISH_UNEXPIRED_EVENT, keyList, valueList));
    }

    /**
     * 发布一个已到期的延迟事件.
     *
     * @param event 延迟事件
     * @param now   当前时间
     * @return 发布成功返回true
     */
    private boolean publishExpiredEvent(DistributedDelayEvent event, long now) {
        final long endTime = now + DOUBLE_PUBLISH_DELAY_TIME;

        List<String> keyList = new ArrayList<>(3);
        keyList.add(KEY_DELAY_JOB);
        keyList.add(KEY_DELAY_TIMER);
        keyList.add(KEY_DELAY_QUEUE);

        List<String> valueList = new ArrayList<>(3);
        valueList.add(buildEventId(event));
        valueList.add(String.valueOf(endTime));
        valueList.add(JSON.toJSONString(event, SerializerFeature.WriteClassName));

        return "1".equals(redisTemplate.eval(SCRIPT_PUBLISH_EXPIRED_EVENT, keyList, valueList));
    }


    // ----------------------------------移除事件------------------------------

    @Override
    public boolean remove(DistributedDelayEvent event) {
        List<String> keyList = new ArrayList<>(2);
        keyList.add(KEY_DELAY_JOB);
        keyList.add(KEY_DELAY_TIMER);

        List<String> valueList = new ArrayList<>(1);
        valueList.add(buildEventId(event));

        return "1".equals(redisTemplate.eval(SCRIPT_REMOVE_EVENT, keyList, valueList));
    }


    /**
     * 构建事件的唯一ID。
     * <p>
     * 示例：<code>事件类名:ID</code>
     *
     * @param event 延迟事件
     * @return 唯一ID
     */
    private String buildEventId(DistributedDelayEvent event) {
        String className = event.getClass().getName();
        StringBuilder sb = new StringBuilder(className.length() + 1 + 18);
        return sb.append(className).append(':').append(event.getId()).toString();
    }

}