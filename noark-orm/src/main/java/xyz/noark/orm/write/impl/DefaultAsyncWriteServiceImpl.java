/*
 * Copyright © 2018 www.noark.xyz All Rights Reserved.
 * 
 * 感谢您选择Noark框架，希望我们的努力能为您提供一个简单、易用、稳定的服务器端框架 ！
 * 除非符合Noark许可协议，否则不得使用该文件，您可以下载许可协议文件：
 * 
 * 		http://www.noark.xyz/LICENSE
 *
 * 1.未经许可，任何公司及个人不得以任何方式或理由对本框架进行修改、使用和传播;
 * 2.禁止在本项目或任何子项目的基础上发展任何派生版本、修改版本或第三方版本;
 * 3.无论你对源代码做出任何修改和改进，版权都归Noark研发团队所有，我们保留所有权利;
 * 4.凡侵犯Noark版权等知识产权的，必依法追究其法律责任，特此郑重法律声明！
 */
package xyz.noark.orm.write.impl;

import static xyz.noark.log.LogHelper.logger;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.github.benmanes.caffeine.cache.RemovalCause;
import com.github.benmanes.caffeine.cache.RemovalListener;

import xyz.noark.core.annotation.Autowired;
import xyz.noark.core.exception.DataException;
import xyz.noark.core.thread.NamedThreadFactory;
import xyz.noark.orm.DataConstant;
import xyz.noark.orm.EntityMapping;
import xyz.noark.orm.accessor.DataAccessor;
import xyz.noark.orm.write.AsyncWriteService;
import xyz.noark.orm.write.OperateType;

/**
 * 回写策略的默认实现.
 *
 * @since 3.0
 * @author 小流氓(176543888@qq.com)
 */
public class DefaultAsyncWriteServiceImpl implements AsyncWriteService {
	@Autowired
	private DataAccessor dataAccessor;
	/** 这个定时任务，有空就处理一下数据保存和缓存清理功能 */
	private final static ScheduledExecutorService SCHEDULED_EXECUTOR = new ScheduledThreadPoolExecutor(4, new NamedThreadFactory("async-write-data"));
	/** 异步回写容器缓存 */
	private LoadingCache<Serializable, AsyncWriteContainer> containers;

	@Override
	public void init(final int saveInterval, final int offlineInterval) {
		RemovalListener<Serializable, AsyncWriteContainer> listener = new RemovalListener<Serializable, AsyncWriteContainer>() {
			@Override
			public void onRemoval(Serializable key, AsyncWriteContainer value, RemovalCause cause) {
				logger.debug("销毁{}秒都没有读写操作的异步回写容器， playerId={}", offlineInterval, key);
				value.syncFlush();
				value.close();
			}
		};

		CacheLoader<Serializable, AsyncWriteContainer> loader = new CacheLoader<Serializable, AsyncWriteContainer>() {
			@Override
			public AsyncWriteContainer load(Serializable playerId) {
				logger.debug("创建异步回写容器， playerId={}", playerId);
				return new AsyncWriteContainer(playerId, saveInterval);
			}
		};

		this.containers = Caffeine.newBuilder().expireAfterAccess(offlineInterval, TimeUnit.SECONDS).removalListener(listener).build(loader);
		SCHEDULED_EXECUTOR.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				containers.cleanUp();
			}
		}, offlineInterval, offlineInterval, TimeUnit.SECONDS);
	}

	/**
	 * 智能分析这个实体类的角色Id是多少.
	 * <p>
	 * 需要考虑到@Join的注解，用来处理User实体的
	 * 
	 * @param em 实体类的描述对象.
	 * @param entity 实体对象.
	 * @return 如果这个实体对象中有@IsRoleId则返回此属性的值，否则返回默认的（系统）角色ID
	 */
	private <T> Serializable analysisRoleIdByEntity(EntityMapping<T> em, T entity) {
		// 拥有@IsRoleId的必属性角色的数据
		if (em.getPlayerId() != null) {
			return em.getPlayerIdValue(entity);
		}
		return DefaultId.INSTANCE;
	}

	/**
	 * 数据操作.
	 * 
	 * @param em 实体类的描述对象
	 * @param entity 实体对象
	 * @param type 操作类型
	 */
	private <T> void operationing(EntityMapping<T> em, T entity, OperateType type) {
		Serializable roleId = this.analysisRoleIdByEntity(em, entity);
		AsyncWriteContainer container = containers.get(roleId);
		switch (type) {
		case INSTER:
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

	@Override
	public <T> void insert(EntityMapping<T> em, T entity) {
		this.operationing(em, entity, OperateType.INSTER);
	}

	@Override
	public <T> void delete(EntityMapping<T> em, T entity) {
		this.operationing(em, entity, OperateType.DELETE);
	}

	@Override
	public <T> void deleteAll(EntityMapping<T> em, List<T> result) {
		for (T entity : result) {
			this.operationing(em, entity, OperateType.DELETE);
		}
	}

	@Override
	public <T> void update(EntityMapping<T> em, T entity) {
		this.operationing(em, entity, OperateType.UPDATE);
	}

	@Override
	public void syncFlushAll() {
		for (AsyncWriteContainer container : containers.asMap().values()) {
			container.syncFlush();
		}
	}

	@Override
	public void shutdown() {
		logger.info("开始通知数据保存任务线程池关闭.");
		SCHEDULED_EXECUTOR.shutdown();
		try {
			// 尝试等待10分钟回写操作，10分钟都没写完就全停掉吧，不写了
			if (!SCHEDULED_EXECUTOR.awaitTermination(DataConstant.SHUTDOWN_MAX_TIME, TimeUnit.MINUTES)) {
				SCHEDULED_EXECUTOR.shutdownNow();
			}
			logger.info("数据保存任务线程池已全部回写完，关闭成功.");
		} catch (InterruptedException ie) {
			logger.error("数据保存任务线程池停机时发生异常.", ie);
			SCHEDULED_EXECUTOR.shutdownNow();
			Thread.currentThread().interrupt();
		}
	}

	/**
	 * 异步回写容器.
	 * 
	 * @author 小流氓(176543888@qq.com)
	 */
	private class AsyncWriteContainer implements Runnable {
		private final Serializable playerId;
		/** 当前已修改过的数据 */
		private Map<String, EntityOperate<?>> entityOperates = new HashMap<>();
		/** 最终需要保存的数据 */
		private Map<String, EntityOperate<?>> flushOperates;
		private final ReentrantLock dataUpdateLock = new ReentrantLock();
		private final ReentrantLock dataFlushLock = new ReentrantLock();
		/** 记录异步操作的结果，以便有需求时，操纵这个结果 */
		private final ScheduledFuture<?> future;

		private AsyncWriteContainer(Serializable playerId, int saveInterval) {
			this.playerId = playerId;
			this.future = SCHEDULED_EXECUTOR.scheduleAtFixedRate(this, saveInterval, saveInterval, TimeUnit.SECONDS);
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
			Map<String, EntityOperate<?>> updateData = null;
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
				for (Entry<String, EntityOperate<?>> e : updateData.entrySet()) {
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
							logger.info("开始保存数据，playerId={}", playerId);
							for (EntityOperate<?> opx : flushOperates.values()) {
								try {
									@SuppressWarnings("unchecked")
									EntityOperate<T> op = (EntityOperate<T>) opx;
									if (op.isDelete()) {
										// 那就是删除操作
										dataAccessor.delete(op.getEntityMapping(), op.getEntity());
									} else if (op.isInsert()) {
										// 插入
										dataAccessor.insert(op.getEntityMapping(), op.getEntity());
									} else if (op.isUpdate()) {
										// 修改
										dataAccessor.update(op.getEntityMapping(), op.getEntity());
									} else {
										throw new DataException("未知的操作实现...");
									}
								} catch (Exception ex) {
									logger.error("保存实体时数据异常，playerId={}{}", playerId, ex);
									logger.error("保存实体时的异常数据 entity={}", opx.getEntity());
								}
							}
							logger.info("保存数据完成，playerId={}", playerId);
						}
					} finally {
						this.flushOperates = null;
					}
				}
			} finally {
				dataFlushLock.unlock();
			}
		}

		@Override
		public void run() {
			try {
				this.syncFlush();
			} catch (Exception e) {// 每次保存必需保证定时器不能停了.
				logger.error("保存个人数据时异常，playerId=" + playerId, e);
			}
		}

		public void close() {
			this.future.cancel(true);
		}
	}

	@Override
	public void asyncFlushByPlayerId(Serializable roleId) {
		AsyncWriteContainer container = containers.get(roleId);
		if (container != null) {
			SCHEDULED_EXECUTOR.submit(container);
		}
	}
}