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
package xyz.noark.orm.accessor.sql;

import static xyz.noark.log.LogHelper.logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import xyz.noark.core.annotation.Value;
import xyz.noark.core.exception.DataAccessException;
import xyz.noark.core.exception.DataException;
import xyz.noark.core.util.StringUtils;
import xyz.noark.orm.DataConstant;
import xyz.noark.orm.DataModular;
import xyz.noark.orm.EntityMapping;
import xyz.noark.orm.FieldMapping;
import xyz.noark.orm.accessor.AbstractDataAccessor;

/**
 * SQL存储策略入口.
 *
 * @since 3.0
 * @author 小流氓(176543888@qq.com)
 */
public abstract class AbstractSqlDataAccessor extends AbstractDataAccessor {
	protected final SqlExpert expert;
	protected final DataSource dataSource;
	/** 是否输出执行SQL日志 */
	@Value(DataModular.DATA_SQL_LOG_ENABLE)
	protected boolean statementExecutableSqlLogEnable = false;
	/** 是否输出执行SQL的参数日志(上一个必需要true) */
	@Value(DataModular.DATA_SQL_LOG_PARAMETER_ENABLE)
	protected boolean statementParameterSetLogEnable = false;
	/** 慢查询时间阀值(单位：毫秒),如果为0则不监控 */
	@Value(DataModular.DATA_SLOW_QUERY_SQL_MILLIS)
	protected int slowQuerySqlMillis = 0;
	/** 自动删除表中多余的字段 */
	@Value(DataModular.DATA_AUTO_ALTER_TABLE_DROP_COLUMN)
	private boolean autoAlterTableDropColumn = false;

	public AbstractSqlDataAccessor(SqlExpert expert, DataSource dataSource) {
		this.expert = expert;
		this.dataSource = dataSource;
	}

	@Override
	public void judgeAccessType() {
		Jdbcs.judgeAccessType(dataSource);
	}

	public void setStatementExecutableSqlLogEnable(boolean statementExecutableSqlLogEnable) {
		this.statementExecutableSqlLogEnable = statementExecutableSqlLogEnable;
	}

	public void setStatementParameterSetLogEnable(boolean statementParameterSetLogEnable) {
		this.statementParameterSetLogEnable = statementParameterSetLogEnable;
	}

	public void setSlowQuerySqlMillis(int slowQuerySqlMillis) {
		this.slowQuerySqlMillis = slowQuerySqlMillis;
	}

	public void setAutoAlterTableDropColumn(boolean autoAlterTableDropColumn) {
		this.autoAlterTableDropColumn = autoAlterTableDropColumn;
	}

	protected <T> T executeStatement(StatementCallback<T> action) {
		try (Connection con = dataSource.getConnection(); Statement stmt = con.createStatement()) {
			return action.doInStatement(stmt);
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}
	}

	protected <T> T execute(PreparedStatementCallback<T> action, String sql) {
		long startTime = slowQuerySqlMillis > 0 ? System.nanoTime() : 0;
		try (Connection con = dataSource.getConnection(); PreparedStatement pstmt = con.prepareStatement(sql)) {
			PreparedStatementProxy proxy = new PreparedStatementProxy(pstmt, statementParameterSetLogEnable);
			T result = action.doInPreparedStatement(proxy);
			this.logExecutableSql(proxy, sql, startTime);
			return result;
		} catch (Exception e) {
			throw new DataAccessException(e);
		}
	}

	protected <T> T executeBatch(PreparedStatementCallback<T> action, String sql) {
		long startTime = slowQuerySqlMillis > 0 ? System.nanoTime() : 0;
		// 批量执行，如果出现异常，数据将进行回滚
		try (Connection con = dataSource.getConnection(); PreparedStatement pstmt = con.prepareStatement(sql)) {
			// 关闭自动提交功能
			con.setAutoCommit(false);
			try {
				// 构建代理，拼接参数
				PreparedStatementProxy proxy = new PreparedStatementProxy(pstmt, statementParameterSetLogEnable);
				T result = action.doInPreparedStatement(proxy);
				// 手动提交
				con.commit();
				// 记录日志
				this.logExecutableSql(proxy, sql, startTime);
				return result;
			} catch (SQLException e) {
				con.rollback();
				throw new DataAccessException(e);
			} finally {
				// 还原为自动提交
				con.setAutoCommit(true);
			}
		} catch (Exception e) {
			throw new DataAccessException(e);
		}
	}

	private void logExecutableSql(PreparedStatementProxy statement, String sql, long startTime) {
		// 不输出，直接忽略所有.
		if (!statementExecutableSqlLogEnable) {
			return;
		}

		StringBuffer formattedSql = new StringBuffer(256);
		if (slowQuerySqlMillis > 0) {
			float execTime = (System.nanoTime() - startTime) / 100_0000F;
			if (execTime >= slowQuerySqlMillis) {
				formattedSql.append("exec sql ").append(execTime).append(" ms.");
			}
		}
		formattedSql.append("\n").append(statementParameterSetLogEnable ? sql.replaceAll("\\?", "{}") : sql);
		// 参数为空，当前操作为批量执行的
		if (statement.getParameters().isEmpty()) {
			logger.debug("batch start...");
			for (List<Object> parameters : statement.getBatchParameterList()) {
				logger.info(formattedSql.toString(), parameters.toArray());
			}
			logger.debug("batch end...");
		}
		// 有参数就是单独执行的
		else {
			logger.info(formattedSql.toString(), statement.getParameters().toArray());
		}
	}

	/**
	 * 判定一个表是否存在.
	 * 
	 * @param tableName 表名
	 * @return 如果存在返回true,否则返回false
	 */
	protected boolean exists(final String tableName) {
		return this.executeStatement((stmt) -> {
			String sql = "SELECT COUNT(1) FROM " + tableName + " where 1!=1";
			try (ResultSet rs = stmt.executeQuery(sql)) {
				return rs.next();
			}
			// 有异常就是表不存在嘛~~~~
			catch (Exception e) {
				return Boolean.FALSE;
			}
		});
	}

	/**
	 * 检查一下表结构是不是跟这个实体一样一样的.
	 */
	@Override
	public synchronized <T> void checkupEntityFieldsWithDatabase(EntityMapping<T> em) {
		// 先判定一下，存不存在
		if (this.exists(em.getTableName())) {
			this.checkEntityTable(em);
		} else {
			// 不存在，直接创建
			this.createEntityTable(em);
		}
	}

	private synchronized <T> void checkEntityTable(final EntityMapping<T> em) {
		this.executeStatement((stmt) -> {
			try (ResultSet rs = stmt.executeQuery(StringUtils.join("SELECT * FROM ", em.getTableName(), " LIMIT 0"))) {
				ResultSetMetaData rsmd = rs.getMetaData();
				// 缓存为Map的方式
				final int len = rsmd.getColumnCount();
				Map<String, Integer> caches = new HashMap<>(len);
				for (int i = 1; i <= len; i++) {
					caches.put(rsmd.getColumnName(i), i);
				}

				// 循环字段检查，如果属性比字段多，就自动补上...
				for (FieldMapping fm : em.getFieldMapping()) {
					// 字段如果有大写字母，则警告提示输出
					if (!fm.getColumnName().equals(fm.getColumnName().toLowerCase())) {
						logger.warn("字段名称中有大写字母,建议修正为下划线命名方式! entity={},field={},columnName={}", em.getEntityClass().getName(), fm.getField().getName(), fm.getColumnName());
					}

					Integer index = caches.remove(fm.getColumnName());
					// 字段不存在，修补字段
					if (index == null) {
						autoAlterTableAddColumn(em, fm);
						tryRepairTextDefaultValue(em, fm);
						continue;
					}

					// 字符串类型的字段，要修正长度的(只能变长，不能变短)
					if (rsmd.getColumnType(index) == Types.VARCHAR) {
						final int length = rsmd.getColumnDisplaySize(index);
						if (fm.getWidth() > length) {
							autoAlterTableUpdateColumn(em, fm);
						} else if (fm.getWidth() < length) {
							logger.warn("表中字段长度大于配置长度，建议手动修正! entity={},field={},length={}", em.getEntityClass().getName(), fm.getField().getName(), fm.getWidth());
						}
					}
				}

				// 还有剩的，那表结构比字段属性多了...
				if (!caches.isEmpty()) {
					// 允许自动删除表中多余的字段...
					if (autoAlterTableDropColumn) {
						caches.keySet().forEach(key -> autoAlterTableDropColumn(em, key));
					}
					// 不允许，那就异常阻止服务启动，把主动权交给研发人员...
					else {
						throw new DataException("表结构字段比实体类属性多. 表[" + em.getTableName() + "]中的属性：" + Arrays.toString(caches.keySet().toArray()));
					}
				}
			}

			// 随便返回一个，没有实际意义
			return 0;
		});
	}

	/** 自动修正字段 */
	private <T> void autoAlterTableUpdateColumn(EntityMapping<T> em, FieldMapping fm) {
		final String sql = expert.genUpdateTableColumnSql(em, fm);
		logger.warn("实体类[{}]对应的数据库表结构不一致，准备自动修补表结构，SQL如下:\n{}", em.getEntityClass(), sql);
		this.executeStatement((stmt) -> stmt.executeUpdate(sql));
	}

	/** 如果是Text智能修补一下默认值 */
	private <T> void tryRepairTextDefaultValue(final EntityMapping<T> em, final FieldMapping fm) {
		// 修正Text字段的默认值.
		if (fm.getWidth() >= DataConstant.COLUMN_MAX_WIDTH && fm.hasDefaultValue()) {
			final String sql = expert.genUpdateDefaultValueSql(em, fm);
			logger.warn("实体类[{}]中的字段[{}]不支持默认值，准备自动修补默认值，SQL如下:\n{}", em.getEntityClass(), fm.getColumnName(), sql);
			this.executeStatement((stmt) -> stmt.executeUpdate(sql));
		}
	}

	/** 自动增加表中不存在的字段 */
	private <T> void autoAlterTableAddColumn(EntityMapping<T> em, FieldMapping fm) {
		final String sql = expert.genAddTableColumnSql(em, fm);
		logger.warn("实体类[{}]对应的数据库表结构不一致，准备自动修补新增的字段，SQL如下:\n{}", em.getEntityClass(), sql);
		this.executeStatement((stmt) -> stmt.executeUpdate(sql));
	}

	/** 自动删除表中不再使用的字段 */
	private <T> void autoAlterTableDropColumn(EntityMapping<T> em, String columnName) {
		String sql = expert.genDropTableColumnSql(em, columnName);
		logger.warn("实体类[{}]对应的数据库表结构不一致，准备自动删除多余字段，SQL如下:\n{}", em.getEntityClass(), sql);
		this.executeStatement((stmt) -> stmt.executeUpdate(sql));
	}

	/**
	 * 创建实体对应的数据库表结构.
	 */
	private synchronized <T> void createEntityTable(EntityMapping<T> em) {
		final String sql = expert.genCreateTableSql(em);
		logger.warn("实体类[{}]对应的数据库表不存在，准备自动创建表结构，SQL如下:\n{}", em.getEntityClass(), sql);
		this.executeStatement((stmt) -> stmt.executeUpdate(sql));
	}
}