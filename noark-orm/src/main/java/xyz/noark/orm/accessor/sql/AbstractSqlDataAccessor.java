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

import javax.sql.DataSource;

import xyz.noark.core.exception.DataAccessException;
import xyz.noark.core.exception.DataException;
import xyz.noark.orm.DataConstant;
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
	protected boolean statementExecutableSqlLogEnable = false;
	/** 是否输出执行SQL的参数日志(上一个必需要true) */
	protected boolean statementParameterSetLogEnable = false;
	/** 慢查询时间阀值(单位：毫秒),如果为0则不监控 */
	protected int slowQuerySqlMillis = 0;

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

	protected <T> T execute(ConnectionCallback<T> action) {
		try (Connection con = dataSource.getConnection()) {
			return action.doInConnection(con);
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}
	}

	protected <T> T execute(StatementCallback<T> action) {
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
		logger.info(formattedSql.toString(), statement.getParameters().toArray());
	}

	/**
	 * 判定一个表是否存在.
	 */
	protected boolean exists(final String tableName) {
		return this.execute(new StatementCallback<Boolean>() {
			@Override
			public Boolean doInStatement(Statement stmt) throws SQLException {
				String sql = "SELECT COUNT(1) FROM " + tableName + " where 1!=1";
				try (ResultSet rs = stmt.executeQuery(sql)) {
					return rs.next();
				} catch (Exception e) {
					// 有异常就是表不存在嘛~~~~
					return Boolean.FALSE;
				}
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
		final String sql = "SELECT * FROM " + em.getTableName() + " LIMIT 1";
		this.execute(new StatementCallback<Void>() {
			@Override
			public Void doInStatement(Statement stmt) throws SQLException {
				try (ResultSet rs = stmt.executeQuery(sql)) {
					ResultSetMetaData rsmd = rs.getMetaData();
					int columnCount = rsmd.getColumnCount();

					// 当表字段比属性多时...
					if (columnCount > em.getFieldMapping().size()) {
						for (int i = 1; i <= columnCount; i++) {
							String columnName = rsmd.getColumnName(i);
							boolean exit = false;
							for (FieldMapping fm : em.getFieldMapping()) {
								if (fm.getColumnName().equals(columnName)) {
									exit = true;
									break;
								} else if (fm.getColumnName().equalsIgnoreCase(columnName)) {
									exit = true;
									String entity = em.getEntityClass().getName();
									String field = fm.getField().getName();
									logger.warn("字段名大小写不匹配,建议修正! table={},column={},entity={},field={}", em.getTableName(), columnName, entity, field);
									break;
								}
							}
							if (!exit) {
								throw new DataException("表结构字段比实体类属性多. 表[" + em.getTableName() + "]中的属性：" + columnName);
							}
						}
					}

					// 循环字段检查，如果属性比字段多，就自动补上...
					for (FieldMapping fm : em.getFieldMapping()) {
						boolean exit = false;
						for (int i = 1; i <= columnCount; i++) {
							String columnName = rsmd.getColumnName(i);
							if (fm.getColumnName().equals(columnName)) {
								exit = true;
								break;
							} else if (fm.getColumnName().equalsIgnoreCase(columnName)) {
								exit = true;
								String entity = em.getEntityClass().getName();
								String field = fm.getField().getName();
								logger.warn("字段名不匹配,建议修正! table={},column={},entity={},field={}", em.getTableName(), columnName, entity, field);
								break;
							}
						}
						if (!exit) {
							// 修补属性
							autoUpdateTable(em, fm);
							tryRepairTextDefaultValue(em, fm);
						}
					}
				}
				return null;
			}
		});
	}

	/** 如果是Text智能修补一下默认值 */
	private <T> void tryRepairTextDefaultValue(final EntityMapping<T> em, final FieldMapping fm) {
		// 修正Text字段的默认值.
		if (fm.getWidth() >= DataConstant.COLUMN_MAX_WIDTH && fm.hasDefaultValue()) {
			final String sql = expert.genUpdateDefaultValueSql(em, fm);
			logger.info("实体类[{}]中的字段[{}]不支持默认值，准备智能修补默认值，SQL如下:\n{}", em.getEntityClass(), fm.getColumnName(), sql);
			this.execute(new StatementCallback<Void>() {
				@Override
				public Void doInStatement(Statement stmt) throws SQLException {
					stmt.executeUpdate(sql);
					return null;
				}
			});
		}
	}

	/** 自动修补表结构 */
	private <T> void autoUpdateTable(final EntityMapping<T> em, final FieldMapping fm) {
		final String sql = expert.genAddTableColumnSql(em, fm);
		logger.info("实体类[{}]对应的数据库表结构不一致，准备自动修补表结构，SQL如下:\n{}", em.getEntityClass(), sql);
		this.execute(new StatementCallback<Void>() {
			@Override
			public Void doInStatement(Statement stmt) throws SQLException {
				stmt.executeUpdate(sql);
				return null;
			}
		});
	}

	/**
	 * 创建实体对应的数据库表结构.
	 */
	private synchronized <T> void createEntityTable(EntityMapping<T> em) {
		final String sql = expert.genCreateTableSql(em);
		logger.warn("实体类[{}]对应的数据库表不存在，准备自动创建表结构，SQL如下:\n{}", em.getEntityClass(), sql);
		this.execute(new StatementCallback<Integer>() {
			@Override
			public Integer doInStatement(Statement stmt) throws SQLException {
				try {
					return stmt.executeUpdate(sql);
				} catch (Exception e) {
					throw new DataAccessException(e);
				}
			}
		});
	}
}