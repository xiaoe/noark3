package xyz.noark.orm.write;

import com.github.benmanes.caffeine.cache.*;
import xyz.noark.core.annotation.Autowired;
import xyz.noark.core.annotation.Value;
import xyz.noark.core.thread.NamedThreadFactory;
import xyz.noark.orm.DataConstant;
import xyz.noark.orm.DataModular;
import xyz.noark.orm.EntityMapping;
import xyz.noark.orm.accessor.DataAccessor;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static xyz.noark.log.LogHelper.logger;

/**
 * 抽象的异步回写中心.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.4
 */
public abstract class AbstractAsyncWriteService implements AsyncWriteService {
    @Value("data.thread.pool.size")
    private int threadPoolSize = 4;
    @Value("data.thread.name.prefix")
    private String threadNamePrefix = "async-write-data";

    @Autowired
    protected DataAccessor dataAccessor;
    /**
     * 定时存档间隔，单位：秒
     */
    @Value(DataModular.DATA_SAVE_INTERVAL)
    protected int saveInterval = 300;
    /**
     * 批量存档数量
     */
    @Value(DataModular.DATA_BATCH_NUM)
    protected int batchOperateNum = 256;
    /**
     * 这个定时任务，有空就处理一下数据保存和缓存清理功能
     */
    protected ScheduledExecutorService scheduledExecutorService;
    /**
     * 异步回写容器缓存
     */
    private LoadingCache<Serializable, AsyncWriteContainer> containers;

    private final int offlineInterval = 3600;

    @Override
    public void init() {
        dataAccessor.judgeAccessType();
        logger.info("初始化数据存储模块，批量存档数量为{},定时存档的时间间隔为{}秒", batchOperateNum, saveInterval);
        // 初始化存档异步线程池
        scheduledExecutorService = new ScheduledThreadPoolExecutor(threadPoolSize, new NamedThreadFactory(threadNamePrefix));

        RemovalListener<Serializable, AsyncWriteContainer> listener = new RemovalListener<Serializable, AsyncWriteContainer>() {
            @Override
            public void onRemoval(Serializable key, AsyncWriteContainer value, RemovalCause cause) {
                logger.info("销毁{}秒都没有读写操作的异步回写容器， playerId={}", offlineInterval, key);
                value.syncFlush();
                value.close();
            }
        };

        CacheLoader<Serializable, AsyncWriteContainer> loader = new CacheLoader<Serializable, AsyncWriteContainer>() {
            @Override
            public AsyncWriteContainer load(Serializable playerId) {
                logger.info("创建异步回写容器， playerId={}", playerId);
                return new AsyncWriteContainer(playerId, saveInterval, scheduledExecutorService, dataAccessor, batchOperateNum);
            }
        };

        this.containers = Caffeine.newBuilder().expireAfterAccess(offlineInterval, TimeUnit.SECONDS).removalListener(listener).build(loader);
        scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                containers.cleanUp();
            }
        }, offlineInterval, offlineInterval, TimeUnit.SECONDS);
    }

    @Override
    public <T> void insert(EntityMapping<T> em, T entity) {
        this.operation(em, entity, OperateType.INSERT);
    }

    @Override
    public <T> void delete(EntityMapping<T> em, T entity) {
        this.operation(em, entity, OperateType.DELETE);
    }

    @Override
    public <T> void deleteAll(EntityMapping<T> em, List<T> result) {
        for (T entity : result) {
            this.operation(em, entity, OperateType.DELETE);
        }
    }

    @Override
    public <T> void update(EntityMapping<T> em, T entity) {
        this.operation(em, entity, OperateType.UPDATE);
    }

    /**
     * 实体类对象的操作.
     *
     * @param em     实体对象描述类
     * @param entity 实体类对象
     * @param type   操作类型
     * @param <T>    实体类
     */
    protected <T> void operation(EntityMapping<T> em, T entity, OperateType type) {
        Serializable groupId = this.analysisGroupIdByEntity(em, entity);
        AsyncWriteContainer container = containers.get(groupId);
        switch (type) {
            case INSERT:
                container.insert(em, entity);
                break;
            case DELETE:
                container.delete(em, entity);
                break;
            case UPDATE:
                container.update(em, entity);
                break;
            default:
                logger.warn("这是要干嘛？ type={},entity={}", type, entity);
                break;
        }
    }

    /**
     * 智能分析这个实体类的角色Id是多少.
     * <p>
     *
     * @param <T>    实体类型
     * @param em     实体类的描述对象.
     * @param entity 实体对象.
     * @return 如果这个实体对象中有@PlayerId则返回此属性的值，否则返回默认的（系统）角色ID
     */
    protected abstract <T> Serializable analysisGroupIdByEntity(EntityMapping<T> em, T entity);

    @Override
    public void shutdown() {
        logger.info("开始通知数据保存任务线程池关闭.");
        this.syncFlushAll();
        scheduledExecutorService.shutdown();
        try {
            // 尝试等待10分钟回写操作，10分钟都没写完就全停掉吧，不写了
            if (!scheduledExecutorService.awaitTermination(DataConstant.SHUTDOWN_MAX_TIME, TimeUnit.MINUTES)) {
                scheduledExecutorService.shutdownNow();
            }
            logger.info("数据保存任务线程池已全部回写完，关闭成功.");
        } catch (InterruptedException ie) {
            logger.error("数据保存任务线程池停机时发生异常.", ie);
            scheduledExecutorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    public void syncFlushAll() {
        for (AsyncWriteContainer container : containers.asMap().values()) {
            container.syncFlush();
        }
    }

    public void asyncFlushByGroupId(Serializable groupId) {
        AsyncWriteContainer container = containers.get(groupId);
        if (container != null) {
            scheduledExecutorService.submit(container);
        }
    }
}