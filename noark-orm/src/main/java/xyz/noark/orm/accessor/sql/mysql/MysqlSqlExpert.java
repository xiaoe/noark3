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

import xyz.noark.orm.EntityMapping;
import xyz.noark.orm.FieldMapping;
import xyz.noark.orm.accessor.FieldType;
import xyz.noark.orm.accessor.sql.AbstractSqlExpert;
import xyz.noark.util.StringUtils;

/**
 * Mysql
 *
 * @since 3.0
 * @author 小流氓(176543888@qq.com)
 */
public class MysqlSqlExpert extends AbstractSqlExpert {

	@Override
	public <T> String genCreateTableSql(EntityMapping<T> em) {
		StringBuilder sb = new StringBuilder(512);
		sb.append("CREATE TABLE `" + em.getTableName() + "` (");
		// 创建字段
		for (FieldMapping fm : em.getFieldMapping()) {
			sb.append('\n').append('`').append(fm.getColumnName()).append('`');
			sb.append(' ').append(evalFieldType(fm));
			// 主键的 @Id，应该加入唯一性约束
			if (fm.isPrimaryId()) {
				sb.append(" UNIQUE NOT NULL");
				// if (fm.isAutoIncrement()) {
				// sb.append(" AUTO_INCREMENT");
				// }
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
					case AsLong:
					case AsDouble:
					case AsFloat:
					case AsAtomicInteger:
						sb.append(" DEFAULT ").append(fm.getDefaultValue()).append("");
						break;
					default:
						if (fm.getWidth() < 65535) {// 超过这个值当Text啦，Text是不可以有默认值的.
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
		if (em.getPlayerId() != null && pk != null && !em.getPlayerId().getField().equals(pk.getField())) {
			sb.append(',').append('\n');
			sb.append("INDEX INDEX_UD (");
			sb.append('`').append(em.getPlayerId().getColumnName()).append('`').append(',');
			sb.setCharAt(sb.length() - 1, ')');
		}
		sb.append("\n ");

		// 结束表字段设置
		sb.setCharAt(sb.length() - 1, ')');
		// 设置特殊引擎
		sb.append(" ENGINE=InnoDB DEFAULT CHARSET=utf8");
		// 表名注释
		if (!StringUtils.isEmpty(em.getTableComment())) {
			sb.append(" COMMENT='").append(em.getTableComment()).append("'");
		}
		return sb.append(";").toString();
	}

	@Override
	protected String evalFieldType(FieldMapping fm) {
		switch (fm.getType()) {

		case AsInteger:// 游戏嘛，数字就是int(11)不要想多啦，简单直接明了
		case AsAtomicInteger:
			return "INT(11)";

		case AsLong:// 龙哥说20就20吧~~~
			return "BIGINT(20)";

		case AsDouble:// 有小数的就直接写上他写的参数
			return "DOUBLE(" + fm.getPrecision() + "," + fm.getScale() + ")";

		case AsFloat:
			return "FLOAT(" + fm.getPrecision() + "," + fm.getScale() + ")";

		default:// 其它的参照默认字段规则 ...
			return super.evalFieldType(fm);
		}
	}

	@Override
	public <T> String genInsertSql(EntityMapping<T> em) {
		// INSERT [LOW_PRIORITY | DELAYED] [IGNORE]
		// [INTO] tbl_name [(col_name,...)]
		// VALUES (expression,...),(...),...
		StringBuilder sb = new StringBuilder(128);
		sb.append("INSERT INTO ").append(em.getTableName()).append(" (");

		int count = 0;
		for (FieldMapping fm : em.getFieldMapping()) {
			sb.append(fm.getColumnName()).append(',');
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
		sb.append("DELETE FROM ").append(sem.getTableName());
		sb.append(" WHERE ").append(sem.getPrimaryId().getColumnName()).append("=?");
		return sb.toString();
	}

	@Override
	public <T> String genUpdateSql(EntityMapping<T> em) {
		StringBuilder sb = new StringBuilder(128);
		sb.append("UPDATE ").append(em.getTableName()).append(" SET ");
		for (FieldMapping fm : em.getFieldMapping()) {
			if (!fm.isPrimaryId()) {
				sb.append(fm.getColumnName()).append("=?,");
			}
		}
		sb.setCharAt(sb.length() - 1, ' ');

		sb.append("WHERE ").append(em.getPrimaryId().getColumnName()).append("=?");
		return sb.toString();
	}

	@Override
	public <T> String genSeleteByPlayerId(EntityMapping<T> em) {
		// Selete id from item where role_id = ?
		StringBuilder sb = new StringBuilder(128);
		sb.append("SELECT ");
		for (FieldMapping fm : em.getFieldMapping()) {
			sb.append(fm.getColumnName()).append(',');
		}
		sb.setCharAt(sb.length() - 1, ' ');
		sb.append("FROM ").append(em.getTableName());
		if (em.getPlayerId() != null) {
			sb.append(" WHERE ").append(em.getPlayerId().getColumnName()).append("=?");
		}
		return sb.toString();
	}

	@Override
	public <T> String genSeleteSql(EntityMapping<T> sem) {
		// Selete id from item where role_id = ?
		StringBuilder sb = new StringBuilder(128);
		sb.append("SELECT ");
		for (FieldMapping fm : sem.getFieldMapping()) {
			sb.append(fm.getColumnName()).append(',');
		}
		sb.setCharAt(sb.length() - 1, ' ');
		sb.append("FROM ").append(sem.getTableName());
		sb.append(" WHERE ").append(sem.getPrimaryId().getColumnName()).append("=?");
		return sb.toString();
	}

	@Override
	public <T> String genSeleteAllSql(EntityMapping<T> sem) {
		StringBuilder sb = new StringBuilder(128);
		sb.append("SELECT ");
		for (FieldMapping fm : sem.getFieldMapping()) {
			sb.append(fm.getColumnName()).append(',');
		}
		sb.setCharAt(sb.length() - 1, ' ');
		sb.append("FROM ").append(sem.getTableName());
		return sb.toString();
	}

	private <T> void handleSingleQuotationMarks(StringBuilder sb, EntityMapping<T> em, FieldMapping fm, T entity) {
		switch (fm.getType()) {
		case AsBoolean:
		case AsInteger:
		case AsLong:
		case AsFloat:
		case AsDouble:
		case AsAtomicInteger:
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
		sb.append("INSERT DELAYED INTO ").append(em.getTableName()).append(" (");
		for (FieldMapping fm : em.getFieldMapping()) {
			sb.append(fm.getColumnName()).append(',');
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
				sb.append(fm.getColumnName()).append("=");
				this.handleSingleQuotationMarks(sb, em, fm, entity);
				sb.append(",");
			}
		}
		sb.setCharAt(sb.length() - 1, ' ');

		sb.append("WHERE ").append(em.getPrimaryId().getColumnName()).append("=");
		this.handleSingleQuotationMarks(sb, em, em.getPrimaryId(), entity);
		return sb.toString();
	}

	@Override
	public <T> String genDeleteSql(EntityMapping<T> em, T entity) {
		StringBuilder sb = new StringBuilder(128);
		sb.append("DELETE FROM ").append(em.getTableName());
		sb.append(" WHERE ").append(em.getPrimaryId().getColumnName()).append("=");
		this.handleSingleQuotationMarks(sb, em, em.getPrimaryId(), entity);
		return sb.toString();
	}

	@Override
	public <T> String genAddTableColumnSql(EntityMapping<T> em, FieldMapping fm) {
		// alter table `user_movement_log` Add column GatewayId int not null
		// default 0 AFTER `Regionid` (在哪个字段后面添加)
		StringBuilder sb = new StringBuilder(128);
		sb.append("ALTER TABLE `").append(em.getTableName()).append("` ADD COLUMN `").append(fm.getColumnName());
		sb.append("` ").append(evalFieldType(fm));
		if (fm.isNotNull()) {
			sb.append(" NOT NULL");
		} else if (fm.getType() == FieldType.AsDate) {
			sb.append(" NULL");
		}

		if (fm.hasDefaultValue()) {
			switch (fm.getType()) {
			case AsBoolean:
			case AsInteger:
			case AsLong:
			case AsDouble:
			case AsFloat:
			case AsAtomicInteger:
				sb.append(" DEFAULT ").append(fm.getDefaultValue()).append("");
				break;
			default:
				if (fm.getWidth() < 65535) {// 超过这个值当Text啦，Text是不可以有默认值的.
					sb.append(" DEFAULT '").append(fm.getDefaultValue()).append("'");
				}
				break;
			}
		}
		if (fm.hasColumnComment()) {
			sb.append(" COMMENT '").append(fm.getColumnComment()).append("'");
		}
		return sb.toString();
	}

	@Override
	public <T> String genUpdateDefaultValueSql(EntityMapping<T> em, FieldMapping fm) {
		StringBuilder sb = new StringBuilder(64);
		sb.append("UPDATE ").append(em.getTableName()).append(" SET ").append(fm.getColumnName()).append("='").append(fm.getDefaultValue()).append("'");
		return sb.toString();
	}
}