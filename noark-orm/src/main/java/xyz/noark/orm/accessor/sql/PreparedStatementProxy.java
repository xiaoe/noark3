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

import xyz.noark.orm.FieldMapping;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * PreparedStatement代理对象.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.0
 */
public class PreparedStatementProxy {
    private final PreparedStatement pstmt;
    private final boolean autoAlterColumnLength;
    private final boolean statementParameterSetLogEnable;

    /**
     * 批量级别的参数列表
     */
    private final List<List<Object>> batchParameterList = new LinkedList<>();
    /**
     * 记录本次存档每个字段的长度
     */
    private final Map<String, Integer> columnMaxLenMap;
    /**
     * 非批量的参数列表
     */
    private List<Object> parameters = new LinkedList<>();

    public PreparedStatementProxy(PreparedStatement pstmt, boolean statementParameterSetLogEnable, boolean autoAlterColumnLength, Map<String, Integer> columnMaxLenMap) {
        this.pstmt = pstmt;
        this.statementParameterSetLogEnable = statementParameterSetLogEnable;
        this.autoAlterColumnLength = autoAlterColumnLength;
        this.columnMaxLenMap = columnMaxLenMap;
    }

    public int executeUpdate() throws SQLException {
        return pstmt.executeUpdate();
    }

    public void addBatch() throws SQLException {
        pstmt.addBatch();
        batchParameterList.add(parameters);
        this.parameters = new LinkedList<>();
    }

    public int[] executeBatch() throws SQLException {
        return pstmt.executeBatch();
    }

    public ResultSet executeQuery() throws SQLException {
        return pstmt.executeQuery();
    }

    public void setObject(int parameterIndex, Object x) throws SQLException {
        if (statementParameterSetLogEnable) {
            parameters.add(x);
        }
        pstmt.setObject(parameterIndex, x);
    }

    public void setString(FieldMapping fm, int parameterIndex, String x) throws SQLException {
        if (statementParameterSetLogEnable) {
            parameters.add("'" + x + "'");
        }
        pstmt.setString(parameterIndex, x);

        // 只记录字符串类型的
        if (autoAlterColumnLength) {
            columnMaxLenMap.put(fm.getColumnName(), x == null ? 0 : x.length());
        }
    }

    public void setLong(int parameterIndex, Long x) throws SQLException {
        if (statementParameterSetLogEnable) {
            parameters.add(x);
        }
        pstmt.setLong(parameterIndex, x);
    }

    public void setInt(int parameterIndex, Integer x) throws SQLException {
        if (statementParameterSetLogEnable) {
            parameters.add(x);
        }
        pstmt.setInt(parameterIndex, x);
    }

    public void setBoolean(int parameterIndex, Boolean x) throws SQLException {
        if (statementParameterSetLogEnable) {
            parameters.add(x);
        }
        pstmt.setBoolean(parameterIndex, x);
    }

    public void setFloat(int parameterIndex, Float x) throws SQLException {
        if (statementParameterSetLogEnable) {
            parameters.add(x);
        }
        pstmt.setFloat(parameterIndex, x);
    }

    public void setDouble(int parameterIndex, Double x) throws SQLException {
        if (statementParameterSetLogEnable) {
            parameters.add(x);
        }
        pstmt.setDouble(parameterIndex, x);
    }

    public void setNull(int parameterIndex, int sqlType) throws SQLException {
        if (statementParameterSetLogEnable) {
            parameters.add(null);
        }
        pstmt.setNull(parameterIndex, sqlType);
    }

    public void setTimestamp(int parameterIndex, Timestamp x) throws SQLException {
        if (statementParameterSetLogEnable) {
            parameters.add("'" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(x) + "'");
        }
        pstmt.setTimestamp(parameterIndex, x);
    }

    public List<Object> getParameters() {
        return parameters;
    }

    public List<List<Object>> getBatchParameterList() {
        return batchParameterList;
    }
}