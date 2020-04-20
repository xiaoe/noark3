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

import xyz.noark.core.exception.DataException;
import xyz.noark.core.util.StringUtils;
import xyz.noark.orm.DataConstant;
import xyz.noark.orm.EntityMapping;
import xyz.noark.orm.FieldMapping;
import xyz.noark.orm.accessor.sql.AbstractSqlDataAccessor;
import xyz.noark.orm.accessor.sql.PreparedStatementCallback;
import xyz.noark.orm.accessor.sql.PreparedStatementProxy;
import xyz.noark.orm.accessor.sql.mysql.adaptor.AbstractValueAdaptor;
import xyz.noark.orm.accessor.sql.mysql.adaptor.ValueAdaptorManager;

import javax.sql.DataSource;
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static xyz.noark.log.LogHelper.logger;

/**
 * MySQL数据访问类.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.0
 */
public class MysqlDataAccessor extends AbstractSqlDataAccessor {

    public MysqlDataAccessor(DataSource dataSource) {
        super(new MysqlSqlExpert(), dataSource);
    }

    @Override
    protected void handleDataTooLongException(EntityMapping<?> em, Map<String, Integer> columnMaxLenMap) {
        // 修正这个字段的长度...
        this.executeStatement((stmt) -> {
            try (ResultSet rs = stmt.executeQuery(StringUtils.join("SELECT * FROM ", em.getTableName(), " LIMIT 0"))) {
                final ResultSetMetaData rsmd = rs.getMetaData();
                for (int i = 1, len = rsmd.getColumnCount(); i <= len; i++) {
                    // 字符串类型的字段，要修正长度的(只能变长，不能变短)
                    final int columnType = rsmd.getColumnType(i);
                    if (columnType == Types.VARCHAR || columnType == Types.LONGVARCHAR) {
                        final String columnName = rsmd.getColumnName(i);
                        final int max = rsmd.getColumnDisplaySize(i);
                        final int length = columnMaxLenMap.getOrDefault(columnName, 0);
                        if (length > max) {
                            em.getFieldMapping().stream().filter(v -> v.getColumnName().equals(columnName)).findFirst().ifPresent(fm -> {
                                int width = 0;
                                // VARCHAR扩容方式，小于512的*2，大于512的+512
                                if (columnType == Types.VARCHAR) {
                                    if (length <= 512) {
                                        width = length * 2;
                                    } else {
                                        width = length + 512;
                                    }
                                }
                                // Text扩容方式，直接升一级
                                else if (columnType == Types.LONGVARCHAR) {
                                    width = DataConstant.TEXT_MAX_WIDTH + 1;
                                }

                                // 有扩容需求
                                if (width > 0) {
                                    fm.setWidth(width);
                                    logger.warn("智能修正字段长度 column={}, before={}, after={}", columnName, max, width);
                                    autoAlterTableUpdateColumn(em, fm);
                                }
                                // 超出了想象的情况
                                else {
                                    logger.error("发现了扩容不了情况，请分析参数 columnName={}, dbMax={}, length={}", columnName, max, length);
                                }
                            });
                        }
                    }
                }
            }
            // 随便返回一个，没有实际意义
            return 0;
        });
    }

    @Override
    public <T> int insert(final EntityMapping<T> em, final T entity) {
        class InsertPreparedStatementCallback implements PreparedStatementCallback<Integer> {
            @Override
            public Integer doInPreparedStatement(PreparedStatementProxy pstmt) throws Exception {
                buildInsertParameter(em, entity, pstmt);
                return pstmt.executeUpdate();
            }
        }
        return execute(em, new InsertPreparedStatementCallback(), expert.genInsertSql(em), true);
    }

    @Override
    public <T> int[] batchInsert(final EntityMapping<T> em, final List<T> entitys) {
        class InsertPreparedStatementCallback implements PreparedStatementCallback<int[]> {
            @Override
            public int[] doInPreparedStatement(PreparedStatementProxy pstmt) throws Exception {
                for (T entity : entitys) {
                    buildInsertParameter(em, entity, pstmt);
                    pstmt.addBatch();
                }
                return pstmt.executeBatch();
            }
        }
        return executeBatch(em, new InsertPreparedStatementCallback(), expert.genInsertSql(em), true);
    }

    private <T> void buildInsertParameter(EntityMapping<T> em, T entity, PreparedStatementProxy pstmt) throws Exception {
        int index = 1;
        for (FieldMapping fm : em.getFieldMapping()) {
            setPstmtParameter(em, fm, pstmt, entity, index++);
        }
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
        return execute(em, new DeletePreparedStatementCallback(), expert.genDeleteSql(em), true);
    }

    @Override
    public <T> int[] batchDelete(EntityMapping<T> em, List<T> entitys) {
        class DeletePreparedStatementCallback implements PreparedStatementCallback<int[]> {
            @Override
            public int[] doInPreparedStatement(PreparedStatementProxy pstmt) throws SQLException {
                for (T entity : entitys) {
                    pstmt.setObject(1, em.getPrimaryIdValue(entity));
                    pstmt.addBatch();
                }
                return pstmt.executeBatch();
            }
        }
        return executeBatch(em, new DeletePreparedStatementCallback(), expert.genDeleteSql(em), true);
    }

    @Override
    public <T> int update(final EntityMapping<T> em, final T entity) {
        class UpdatePreparedStatementCallback implements PreparedStatementCallback<Integer> {
            @Override
            public Integer doInPreparedStatement(PreparedStatementProxy pstmt) throws Exception {
                buildUpdateParameter(em, entity, pstmt);
                return pstmt.executeUpdate();
            }
        }
        return execute(em, new UpdatePreparedStatementCallback(), expert.genUpdateSql(em), true);
    }

    @Override
    public <T> int[] batchUpdate(EntityMapping<T> em, List<T> entitys) {
        class UpdatePreparedStatementCallback implements PreparedStatementCallback<int[]> {
            @Override
            public int[] doInPreparedStatement(PreparedStatementProxy pstmt) throws Exception {
                for (T entity : entitys) {
                    buildUpdateParameter(em, entity, pstmt);
                    pstmt.addBatch();
                }
                return pstmt.executeBatch();
            }
        }
        return executeBatch(em, new UpdatePreparedStatementCallback(), expert.genUpdateSql(em), true);
    }

    private <T> void buildUpdateParameter(EntityMapping<T> em, T entity, PreparedStatementProxy pstmt) throws Exception {
        int index = 1;
        // 非主键
        for (FieldMapping fm : em.getFieldMapping()) {
            if (fm.isPrimaryId()) {
                continue;
            }
            setPstmtParameter(em, fm, pstmt, entity, index++);
        }
        // 主键
        setPstmtParameter(em, em.getPrimaryId(), pstmt, entity, index);
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
        return execute(em, new LoadPreparedStatementCallback(), expert.genSelectSql(em), true);
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
        return execute(em, new LoadAllPreparedStatementCallback(), expert.genSelectAllSql(em), true);
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
        return execute(em, new LoadByPlayerIdIdPreparedStatementCallback(), expert.genSelectByPlayerId(em), true);
    }

    private <T> void setPstmtParameter(EntityMapping<T> em, FieldMapping fm, PreparedStatementProxy pstmt, final T entity, final int index) throws Exception {
        AbstractValueAdaptor<?> adaptor = ValueAdaptorManager.getValueAdaptor(fm.getType());
        adaptor.parameterToPreparedStatement(em, fm, pstmt, entity, index);
    }
}