/*
 * Copyright © 2018 www.noark.xyz All Rights Reserved.
 *
 * 感谢您选择Noark框架，希望我们的努力能为您提供一个简单、易用、稳定的服务器端框架 ！
 * 除非符合Noark许可协议，否则不得使用该文件，您可以下载许可协议文件：
 *
 *        http://www.noark.xyz/LICENSE
 *
 * 1.未经许可，任何公司及个人不得以任何方式或理由对本框架进行修改、使用和传播;
 * 2.禁止在本项目或任何子项目的基础上发展任何派生版本、修改版本或第三方版本;
 * 3.无论你对源代码做出任何修改和改进，版权都归Noark研发团队所有，我们保留所有权利;
 * 4.凡侵犯Noark版权等知识产权的，必依法追究其法律责任，特此郑重法律声明！
 */
package xyz.noark.orm.accessor.sql.mysql;

import xyz.noark.core.util.StringUtils;
import xyz.noark.orm.DataConstant;
import xyz.noark.orm.EntityMapping;
import xyz.noark.orm.FieldMapping;
import xyz.noark.orm.accessor.FieldType;
import xyz.noark.orm.accessor.sql.AbstractSqlExpert;

/**
 * Mysql
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.0
 */
public class MysqlSqlExpert extends AbstractSqlExpert {

    @Override
    public <T> String genCreateTableSql(EntityMapping<T> em) {
        StringBuilder sb = new StringBuilder(512);
        sb.append("CREATE TABLE `").append(em.getTableName()).append("` (");
        // 创建字段
        for (FieldMapping fm : em.getFieldMapping()) {
            sb.append('\n').append('`').append(fm.getColumnName()).append('`');
            sb.append(' ').append(evalFieldType(fm));

            // 如果指定了排序规则，那也要加进去.
            this.buildCollate(sb, fm);

            // 主键的 @Id，应该加入唯一性约束
            if (fm.isPrimaryId()) {
                sb.append(" UNIQUE NOT NULL");
                // 自增主键
                if (fm.hasGeneratedValue()) {
                    sb.append(" AUTO_INCREMENT");
                }
            }
            // 普通字段
            else {
                // 下面的关于Timestamp处理，是因为MySql中第一出现Timestamp的话，如果没有设定default，数据库默认会设置为CURRENT_TIMESTAMP
                if (fm.isNotNull()) {
                    sb.append(" NOT NULL");
                } else if (fm.getType() == FieldType.AsDate) {
                    sb.append(" NULL");
                }

                if (fm.hasDefaultValue()) {
                    switch (fm.getType()) {
                        case AsBoolean:
                        case AsInteger:
                        case AsAtomicInteger:
                        case AsLong:
                        case AsLongAdder:
                        case AsAtomicLong:
                        case AsFloat:
                        case AsDouble:
                            sb.append(" DEFAULT ").append(fm.getDefaultValue());
                            break;
                        // Blob是不可以有默认值的
                        case AsBlob:
                            break;
                        default:
                            // 超过这个值当Text啦，Text是不可以有默认值的.
                            if (fm.getWidth() < DataConstant.VARCHAT_MAX_WIDTH) {
                                sb.append(" DEFAULT '").append(fm.getDefaultValue()).append("'");
                            }
                            break;
                    }
                }
            }

            if (fm.hasColumnComment()) {
                sb.append(" COMMENT '").append(fm.getColumnComment()).append("'");
            }

            sb.append(',');
        }
        // 创建主键
        FieldMapping pk = em.getPrimaryId();
        if (pk != null) {
            sb.append('\n');
            sb.append("PRIMARY KEY (");
            sb.append('`').append(pk.getColumnName()).append('`').append(',');
            sb.setCharAt(sb.length() - 1, ')');
        }
        // 玩家ID的索引
        if (em.getPlayerId() != null && pk != null && !em.getPlayerId().getField().equals(pk.getField())) {
            sb.append(',').append('\n');
            sb.append("INDEX INDEX_UD (");
            sb.append('`').append(em.getPlayerId().getColumnName()).append('`').append(',');
            sb.setCharAt(sb.length() - 1, ')');
        }
        // 结束表字段设置并设置特殊引擎
        sb.append("\n )ENGINE=InnoDB DEFAULT CHARSET=utf8mb4");
        // 表名注释
        if (!StringUtils.isEmpty(em.getTableComment())) {
            sb.append(" COMMENT='").append(em.getTableComment()).append("'");
        }
        return sb.append(";").toString();
    }

    /**
     * 构建排序规则信息.
     *
     * @param sb SQL拼接字符串
     * @param fm 当前属性
     */
    private void buildCollate(StringBuilder sb, FieldMapping fm) {
        if (fm.hasCollate()) {
            sb.append(" COLLATE ").append(fm.getCollateValue());
        }
    }

    @Override
    protected String evalFieldType(FieldMapping fm) {
        switch (fm.getType()) {

            // 游戏嘛，数字就是int(11)不要想多啦，简单直接明了
            case AsInteger:
            case AsAtomicInteger:
                return "INT(11)";

            // 20就20吧~~~
            case AsLong:
            case AsLongAdder:
            case AsAtomicLong:
                return "BIGINT(20)";

            // 有小数的就直接写上他写的参数
            case AsDouble:
                return "DOUBLE(" + fm.getPrecision() + "," + fm.getScale() + ")";

            case AsFloat:
                return "FLOAT(" + fm.getPrecision() + "," + fm.getScale() + ")";

            // 其它的参照默认字段规则 ...
            default:
                return super.evalFieldType(fm);
        }
    }

    @Override
    public <T> String genInsertSql(EntityMapping<T> em) {
        // INSERT [LOW_PRIORITY | DELAYED] [IGNORE]
        // [INTO] tbl_name [(col_name,...)]
        // VALUES (expression,...),(...),...
        StringBuilder sb = new StringBuilder(128);
        sb.append("INSERT INTO ");
        this.append(sb, em.getTableName()).append(" (");

        int count = 0;
        for (FieldMapping fm : em.getFieldMapping()) {
            this.append(sb, fm.getColumnName()).append(',');
            count++;
        }
        sb.setCharAt(sb.length() - 1, ')');

        sb.append(" VALUES (");
        for (int i = 0; i < count; i++) {
            sb.append("?,");
        }
        sb.setCharAt(sb.length() - 1, ')');
        return sb.toString();
    }

    @Override
    public <T> String genDeleteSql(EntityMapping<T> sem) {
        // delete from item where id=?
        StringBuilder sb = new StringBuilder(128);
        sb.append("DELETE FROM ");
        this.append(sb, sem.getTableName()).append(" WHERE ");
        this.append(sb, sem.getPrimaryId().getColumnName()).append("=?");
        return sb.toString();
    }

    @Override
    public <T> String genUpdateSql(EntityMapping<T> em) {
        StringBuilder sb = new StringBuilder(128);
        sb.append("UPDATE ").append(em.getTableName()).append(" SET ");
        for (FieldMapping fm : em.getFieldMapping()) {
            if (!fm.isPrimaryId()) {
                this.append(sb, fm.getColumnName()).append("=?,");
            }
        }
        sb.setCharAt(sb.length() - 1, ' ');

        sb.append("WHERE ");
        this.append(sb, em.getPrimaryId().getColumnName()).append("=?");
        return sb.toString();
    }

    @Override
    public <T> String genSelectByPlayerId(EntityMapping<T> em) {
        // Select id from item where role_id = ?
        StringBuilder sb = new StringBuilder(128);
        sb.append("SELECT ");
        for (FieldMapping fm : em.getFieldMapping()) {
            this.append(sb, fm.getColumnName()).append(',');
        }
        sb.setCharAt(sb.length() - 1, ' ');
        sb.append("FROM ");
        this.append(sb, em.getTableName());
        if (em.getPlayerId() != null) {
            sb.append(" WHERE ");
            this.append(sb, em.getPlayerId().getColumnName()).append("=?");
        }
        return sb.toString();
    }

    @Override
    public <T> String genSelectSql(EntityMapping<T> sem) {
        // Select id from item where role_id = ?
        StringBuilder sb = new StringBuilder(128);
        sb.append("SELECT ");
        for (FieldMapping fm : sem.getFieldMapping()) {
            this.append(sb, fm.getColumnName()).append(',');
        }
        sb.setCharAt(sb.length() - 1, ' ');
        sb.append("FROM ");
        this.append(sb, sem.getTableName());
        sb.append(" WHERE ");
        this.append(sb, sem.getPrimaryId().getColumnName()).append("=?");
        return sb.toString();
    }

    @Override
    public <T> String genSelectAllSql(EntityMapping<T> sem) {
        StringBuilder sb = new StringBuilder(128);
        sb.append("SELECT ");
        for (FieldMapping fm : sem.getFieldMapping()) {
            this.append(sb, fm.getColumnName()).append(',');
        }
        sb.setCharAt(sb.length() - 1, ' ');
        sb.append("FROM ");
        return this.append(sb, sem.getTableName()).toString();
    }

    private <T> void handleSingleQuotationMarks(StringBuilder sb, EntityMapping<T> em, FieldMapping fm, T entity) {
        switch (fm.getType()) {
            case AsBoolean:
            case AsInteger:
            case AsAtomicInteger:
            case AsLong:
            case AsLongAdder:
            case AsAtomicLong:
            case AsFloat:
            case AsDouble:
                sb.append(em.getMethodAccess().invoke(entity, fm.getGetMethodIndex()));
                break;
            default:
                sb.append("'").append(em.getMethodAccess().invoke(entity, fm.getGetMethodIndex())).append("'");
                break;
        }
    }

    @Override
    public <T> String genInsertSql(EntityMapping<T> em, T entity) {
        StringBuilder sb = new StringBuilder(256);
        sb.append("INSERT DELAYED INTO ");
        this.append(sb, em.getTableName()).append(" (");
        for (FieldMapping fm : em.getFieldMapping()) {
            this.append(sb, fm.getColumnName()).append(',');
        }
        sb.setCharAt(sb.length() - 1, ')');

        sb.append(" VALUES (");
        for (FieldMapping fm : em.getFieldMapping()) {
            this.handleSingleQuotationMarks(sb, em, fm, entity);
            sb.append(",");
        }
        sb.setCharAt(sb.length() - 1, ')');
        return sb.toString();
    }

    @Override
    public <T> String genUpdateSql(EntityMapping<T> em, T entity) {
        StringBuilder sb = new StringBuilder(256);
        sb.append("UPDATE ").append(em.getTableName()).append(" SET ");
        for (FieldMapping fm : em.getFieldMapping()) {
            if (!fm.isPrimaryId()) {
                this.append(sb, fm.getColumnName()).append("=");
                this.handleSingleQuotationMarks(sb, em, fm, entity);
                sb.append(",");
            }
        }
        sb.setCharAt(sb.length() - 1, ' ');

        sb.append("WHERE ");
        this.append(sb, em.getPrimaryId().getColumnName()).append("=");
        this.handleSingleQuotationMarks(sb, em, em.getPrimaryId(), entity);
        return sb.toString();
    }

    @Override
    public <T> String genDeleteSql(EntityMapping<T> em, T entity) {
        StringBuilder sb = new StringBuilder(128);
        sb.append("DELETE FROM ");
        this.append(sb, em.getTableName()).append(" WHERE ");
        this.append(sb, em.getPrimaryId().getColumnName()).append("=");
        this.handleSingleQuotationMarks(sb, em, em.getPrimaryId(), entity);
        return sb.toString();
    }

    @Override
    public <T> String genAddTableColumnSql(EntityMapping<T> em, FieldMapping fm) {
        return genAddOrUpdateTableColumnSql(em, fm, false);
    }

    @Override
    public <T> String genUpdateTableColumnSql(EntityMapping<T> em, FieldMapping fm) {
        return genAddOrUpdateTableColumnSql(em, fm, true);
    }

    private <T> String genAddOrUpdateTableColumnSql(EntityMapping<T> em, FieldMapping fm, boolean update) {
        StringBuilder sb = new StringBuilder(128);
        sb.append("ALTER TABLE `").append(em.getTableName()).append("` ").append(update ? "MODIFY" : "ADD").append(" COLUMN `").append(fm.getColumnName());
        sb.append("` ").append(evalFieldType(fm));

        // 如果指定了排序规则，那也要加进去.
        this.buildCollate(sb, fm);

        if (fm.isNotNull()) {
            sb.append(" NOT NULL");
        } else if (fm.getType() == FieldType.AsDate) {
            sb.append(" NULL");
        }

        if (fm.hasDefaultValue()) {
            switch (fm.getType()) {
                case AsBoolean:
                case AsInteger:
                case AsAtomicInteger:
                case AsLong:
                case AsLongAdder:
                case AsAtomicLong:
                case AsFloat:
                case AsDouble:
                    sb.append(" DEFAULT ").append(fm.getDefaultValue());
                    break;
                //Blob是不会有默认值的
                case AsBlob:
                    break;
                default:
                    // 超过这个值当Text啦，Text是不可以有默认值的.
                    if (fm.getWidth() < DataConstant.VARCHAT_MAX_WIDTH) {
                        sb.append(" DEFAULT '").append(fm.getDefaultValue()).append("'");
                    }
                    break;
            }
        }

        // 自增主键
        if (fm.hasGeneratedValue()) {
            sb.append(" AUTO_INCREMENT");
        }

        // 字段描述
        if (fm.hasColumnComment()) {
            sb.append(" COMMENT '").append(fm.getColumnComment()).append("'");
        }
        return sb.toString();
    }

    @Override
    public <T> String genDropTableColumnSql(EntityMapping<T> em, String columnName) {
        // alter table tableName drop column xxx;
        StringBuilder sb = new StringBuilder(128);
        sb.append("ALTER TABLE `").append(em.getTableName()).append("` DROP COLUMN `").append(columnName).append("` ");
        return sb.toString();
    }

    @Override
    public <T> String genUpdateDefaultValueSql(EntityMapping<T> em, FieldMapping fm) {
        StringBuilder sb = new StringBuilder(64);
        sb.append("UPDATE ");
        this.append(sb, em.getTableName()).append(" SET ");
        this.append(sb, fm.getColumnName()).append("='").append(fm.getDefaultValue()).append("'");
        return sb.toString();
    }

    /**
     * 添加字段名字，如果是关键字则要添加反点号...
     *
     * @param sb   StringBuilder对象
     * @param name 字段名称
     * @return 修正关键字的名称
     */
    private StringBuilder append(StringBuilder sb, String name) {
        if (MysqlKeyword.isKeyword(name)) {
            return sb.append("`").append(name).append("`");
        } else {
            return sb.append(name);
        }
    }
}