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
package xyz.noark.orm.accessor.sql.mysql;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import xyz.noark.core.exception.DataException;
import xyz.noark.orm.EntityMapping;
import xyz.noark.orm.FieldMapping;
import xyz.noark.orm.accessor.sql.PreparedStatementCallback;
import xyz.noark.orm.accessor.sql.PreparedStatementProxy;
import xyz.noark.orm.accessor.sql.AbstractSqlDataAccessor;
import xyz.noark.orm.accessor.sql.mysql.adaptor.AbstractValueAdaptor;
import xyz.noark.orm.accessor.sql.mysql.adaptor.ValueAdaptorManager;

/**
 * MySQL数据访问类.
 *
 * @since 3.0
 * @author 小流氓(176543888@qq.com)
 */
public class MysqlDataAccessor extends AbstractSqlDataAccessor {

	public MysqlDataAccessor(DataSource dataSource) {
		super(new MysqlSqlExpert(), dataSource);
	}

	@Override
	public <T> int insert(final EntityMapping<T> em, final T entity) {
		class InsterPreparedStatementCallback implements PreparedStatementCallback<Integer> {
			private int index = 1;

			@Override
			public Integer doInPreparedStatement(PreparedStatementProxy pstmt) throws Exception {
				for (FieldMapping fm : em.getFieldMapping()) {
					setPstmtParameter(em, fm, pstmt, entity, index++);
				}
				return pstmt.executeUpdate();
			}
		}
		return execute(new InsterPreparedStatementCallback(), expert.genInsertSql(em));
	}

	@Override
	public <T> int delete(final EntityMapping<T> em, final T entity) {
		return delete(em, em.getPrimaryIdValue(entity));
	}

	private <K extends Serializable> int delete(final EntityMapping<?> em, final K id) {
		class DeletePreparedStatementCallback implements PreparedStatementCallback<Integer> {
			@Override
			public Integer doInPreparedStatement(PreparedStatementProxy pstmt) throws SQLException {
				pstmt.setObject(1, id);
				return pstmt.executeUpdate();
			}
		}
		return execute(new DeletePreparedStatementCallback(), expert.genDeleteSql(em));
	}

	@Override
	public <T> int update(final EntityMapping<T> em, final T entity) {
		class UpdatePreparedStatementCallback implements PreparedStatementCallback<Integer> {
			private int index = 1;

			@Override
			public Integer doInPreparedStatement(PreparedStatementProxy pstmt) throws Exception {
				// 非主键
				for (FieldMapping fm : em.getFieldMapping()) {
					if (fm.isPrimaryId()) {
						continue;
					}
					setPstmtParameter(em, fm, pstmt, entity, index++);
				}
				// 主键
				setPstmtParameter(em, em.getPrimaryId(), pstmt, entity, index);
				return pstmt.executeUpdate();
			}
		}
		return execute(new UpdatePreparedStatementCallback(), expert.genUpdateSql(em));
	}

	@Override
	public <T, K extends Serializable> T load(final EntityMapping<T> em, final K id) {
		class LoadPreparedStatementCallback implements PreparedStatementCallback<T> {
			@Override
			public T doInPreparedStatement(PreparedStatementProxy pstmt) throws SQLException {
				pstmt.setObject(1, id);
				try (ResultSet rs = pstmt.executeQuery()) {
					return rs.next() ? newEntity(em, rs) : null;
				} catch (Exception e) {
					throw new DataException("加载数据时异常，请查看实体类[" + em.getEntityClass().getName() + "]配置", e);
				}
			}
		}
		return execute(new LoadPreparedStatementCallback(), expert.genSeleteSql(em));
	}

	@Override
	public <T> List<T> loadAll(final EntityMapping<T> em) {
		class LoadAllPreparedStatementCallback implements PreparedStatementCallback<List<T>> {
			@Override
			public List<T> doInPreparedStatement(PreparedStatementProxy pstmt) throws SQLException {
				try (ResultSet rs = pstmt.executeQuery()) {
					return newEntityList(em, rs);
				} catch (Exception e) {
					throw new DataException("加载数据时异常，请查看实体类[" + em.getEntityClass().getName() + "]配置", e);
				}
			}
		}
		return execute(new LoadAllPreparedStatementCallback(), expert.genSeleteAllSql(em));
	}

	public <T> List<T> newEntityList(final EntityMapping<T> em, ResultSet rs) throws Exception {
		List<T> result = new ArrayList<>();
		while (rs.next()) {
			result.add(newEntity(em, rs));
		}
		return result;
	}

	public <T> T newEntity(final EntityMapping<T> em, ResultSet rs) throws Exception {
		T result = em.newEntity();
		for (FieldMapping fm : em.getFieldInfo()) {
			ValueAdaptorManager.getValueAdaptor(fm.getType()).resultSetToParameter(em, fm, rs, result);
		}
		return result;
	}

	@Override
	public <T> List<T> loadAll(EntityMapping<T> em, Serializable playerId) {
		class LoadByPlayerIdIdPreparedStatementCallback implements PreparedStatementCallback<List<T>> {
			@Override
			public List<T> doInPreparedStatement(PreparedStatementProxy pstmt) throws SQLException {
				pstmt.setObject(1, playerId);

				try (ResultSet rs = pstmt.executeQuery()) {
					return newEntityList(em, rs);
				} catch (Exception e) {
					throw new DataException("加载数据时异常，请查看实体类[" + em.getEntityClass().getName() + "]配置", e);
				}
			}
		}
		return execute(new LoadByPlayerIdIdPreparedStatementCallback(), expert.genSeleteByPlayerId(em));
	}

	private <T> void setPstmtParameter(EntityMapping<T> em, FieldMapping fm, PreparedStatementProxy pstmt, final T entity, final int index) throws Exception {
		AbstractValueAdaptor<?> adaptor = ValueAdaptorManager.getValueAdaptor(fm.getType());
		adaptor.parameterToPreparedStatement(em, fm, pstmt, entity, index);
	}
}