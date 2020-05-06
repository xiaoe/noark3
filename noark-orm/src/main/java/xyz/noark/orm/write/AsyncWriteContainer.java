package xyz.noark.orm.write;

import xyz.noark.core.exception.DataException;
import xyz.noark.orm.EntityMapping;
import xyz.noark.orm.accessor.DataAccessor;
import xyz.noark.orm.write.impl.EntityOperate;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import static xyz.noark.log.LogHelper.logger;

/**
 * 异步回写容器.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.4
 */
class AsyncWriteContainer implements Runnable {
    /**
     * 分组存档的ID，可能是玩家ID，也可能是其他一些值
     */
    private final Serializable groupId;
    private final DataAccessor dataAccessor;
    private final int batchOperateNum;
    private final ReentrantLock dataUpdateLock = new ReentrantLock();
    private final ReentrantLock dataFlushLock = new ReentrantLock();
    /**
     * 记录异步操作的结果，以便有需求时，操纵这个结果
     */
    private final ScheduledFuture<?> future;
    /**
     * 当前已修改过的数据
     */
    private Map<String, EntityOperate<?>> entityOperates = new HashMap<>();
    /**
     * 最终需要保存的数据
     */
    private Map<String, EntityOperate<?>> flushOperates;

    AsyncWriteContainer(Serializable groupId, int saveInterval, ScheduledExecutorService scheduledExecutorService, DataAccessor dataAccessor, int batchOperateNum) {
        this.groupId = groupId;
        this.dataAccessor = dataAccessor;
        this.batchOperateNum = batchOperateNum;
        this.future = scheduledExecutorService.scheduleAtFixedRate(this, saveInterval, saveInterval, TimeUnit.SECONDS);
    }

    @SuppressWarnings("unchecked")
    private <T> EntityOperate<T> getEntityOperate(EntityMapping<T> em, T entity) {
        String entityId = em.getPrimaryKey(entity);
        EntityOperate<T> entityOperate = (EntityOperate<T>) entityOperates.get(entityId);
        if (null == entityOperate) {
            entityOperate = new EntityOperate<>(entityId, em);
            entityOperates.put(entityId, entityOperate);
        }
        return entityOperate;
    }

    /**
     * 保存一个新增的数据
     */
    public <T> void insert(EntityMapping<T> em, T entity) {
        dataUpdateLock.lock();
        try {
            EntityOperate<T> entityOperate = getEntityOperate(em, entity);
            entityOperate.insert(entity);
        } finally {
            dataUpdateLock.unlock();
        }
    }

    /**
     * 保存一个修改过的数据.
     */
    public <T> void update(EntityMapping<T> em, T entity) {
        dataUpdateLock.lock();
        try {
            EntityOperate<T> entityOperate = getEntityOperate(em, entity);
            entityOperate.update(entity);
        } finally {
            dataUpdateLock.unlock();
        }
    }

    /**
     * 删除一个数据.
     */
    public <T> void delete(EntityMapping<T> em, T entity) {
        dataUpdateLock.lock();
        try {
            EntityOperate<T> entityOperate = getEntityOperate(em, entity);
            boolean deleted = entityOperate.delete(entity);
            if (deleted) {
                entityOperates.remove(entityOperate.getId());
            }
        } finally {
            dataUpdateLock.unlock();
        }
    }

    private Map<String, EntityOperate<?>> getNewUpdateData() {
        if (entityOperates.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<String, EntityOperate<?>> updateData;
        // 如果变更数据集不为空，就先把他拿出来.
        dataUpdateLock.lock();
        try {
            updateData = entityOperates;
            this.entityOperates = new HashMap<>(32);
        } finally {
            dataUpdateLock.unlock();
        }
        return updateData;
    }

    private void mergeFlushData(Map<String, EntityOperate<?>> updateData) {
        if (flushOperates == null) {
            flushOperates = updateData;
        } else {
            for (Map.Entry<String, EntityOperate<?>> e : updateData.entrySet()) {
                String id = e.getKey();
                EntityOperate<?> existOperate = e.getValue();
                flushOperates.put(id, existOperate);
            }
        }
    }

    /**
     * 同步式回写数据.
     */
    public <T> void syncFlush() {
        dataFlushLock.lock();
        try {
            // 取出最新有过改动的数据
            Map<String, EntityOperate<?>> updateData = this.getNewUpdateData();
            // 合并到要回写的数据里
            this.mergeFlushData(updateData);

            if (flushOperates != null) {
                try {
                    if (!flushOperates.isEmpty()) {
                        logger.info("开始保存数据，groupId={}", groupId);

                        // 数据分组
                        Map<EntityMapping<T>, EnumMap<OperateType, List<T>>> grouping = new HashMap<>(256);
                        for (EntityOperate<?> opx : flushOperates.values()) {
                            @SuppressWarnings("unchecked")
                            EntityOperate<T> op = (EntityOperate<T>) opx;
                            // 一种实体类
                            EnumMap<OperateType, List<T>> category = grouping.computeIfAbsent(op.getEntityMapping(), key -> new EnumMap<>(OperateType.class));
                            // 删除操作
                            if (op.isDelete()) {
                                category.computeIfAbsent(OperateType.DELETE, key -> new LinkedList<>()).add(op.getEntity());
                            }
                            // 插入操作
                            else if (op.isInsert()) {
                                category.computeIfAbsent(OperateType.INSERT, key -> new LinkedList<>()).add(op.getEntity());
                            }
                            // 修改操作
                            else if (op.isUpdate()) {
                                category.computeIfAbsent(OperateType.UPDATE, key -> new LinkedList<>()).add(op.getEntity());
                            }
                            // 未知操作
                            else {
                                throw new DataException("未知的操作实现...");
                            }
                        }

                        // 批量存档
                        for (Map.Entry<EntityMapping<T>, EnumMap<OperateType, List<T>>> e : grouping.entrySet()) {
                            this.autoOperateEntity(OperateType.DELETE, e.getKey(), e.getValue());
                            this.autoOperateEntity(OperateType.UPDATE, e.getKey(), e.getValue());
                            this.autoOperateEntity(OperateType.INSERT, e.getKey(), e.getValue());
                        }
                        logger.info("保存数据完成，groupId={}", groupId);
                    }
                } finally {
                    this.flushOperates = null;
                }
            }
        } finally {
            dataFlushLock.unlock();
        }
    }

    /**
     * 智能分析存档实体对象.
     *
     * @param <T>       实体类型
     * @param type      操作类型
     * @param em        实体映射
     * @param entityMap 实体集合
     */
    private <T> void autoOperateEntity(OperateType type, EntityMapping<T> em, EnumMap<OperateType, List<T>> entityMap) {
        List<T> entityList = entityMap.getOrDefault(type, Collections.emptyList());
        // 没有数据
        if (entityList.isEmpty()) {
            return;
        }

        // 只有一个实体有变化
        if (entityList.size() == 1) {
            this.operateEntity(type, em, entityList.get(0));
            return;
        }

        // 批量操作，那还是转化为ArrayList来切割
        if (entityList.size() > batchOperateNum) {
            entityList = new ArrayList<>(entityList);
        }

        // 分批
        for (int i = 0, len = (entityList.size() - 1) / batchOperateNum + 1; i < len; i++) {
            int start = i * batchOperateNum;
            int end = Math.min(start + batchOperateNum, entityList.size());
            this.batchOperateEntity(type, em, entityList.subList(start, end));
        }
    }

    private <T> void batchOperateEntity(OperateType type, EntityMapping<T> em, List<T> entityList) {
        try {
            switch (type) {
                case INSERT:
                    dataAccessor.batchInsert(em, entityList);
                    break;
                case DELETE:
                    dataAccessor.batchDelete(em, entityList);
                    break;
                case UPDATE:
                    dataAccessor.batchUpdate(em, entityList);
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            logger.debug("批量存档失败，准备逐条存档 e={}", e);
            // 批量失败，那就一个一个来吧...
            for (T entity : entityList) {
                this.operateEntity(type, em, entity);
            }
        }
    }

    private <T> void operateEntity(OperateType type, EntityMapping<T> em, T entity) {
        try {
            switch (type) {
                case INSERT:
                    dataAccessor.insert(em, entity);
                    break;
                case DELETE:
                    dataAccessor.delete(em, entity);
                    break;
                case UPDATE:
                    dataAccessor.update(em, entity);
                    break;
                default:
                    break;
            }
        } catch (Exception exx) {
            logger.error("操作实体时数据异常，groupId={}{}", groupId, exx);
            logger.error("操作实体时的异常数据 entity={}", entity);
        }
    }

    @Override
    public void run() {
        try {
            this.syncFlush();
        } catch (Throwable e) {// 每次保存必需保证定时器不能停了.
            logger.error("保存个人数据时异常，groupId=" + groupId, e);
        }
    }

    public void close() {
        this.future.cancel(true);
    }
}