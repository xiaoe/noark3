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

import java.util.Arrays;
import java.util.HashSet;

/**
 * Mysql关键字.
 *
 * @since 3.2
 * @author 小流氓(176543888@qq.com)
 */
public class MysqlKeyword {
	private static final HashSet<String> KEYWORD = new HashSet<>(Arrays.asList(
			// A开头的.
			"add", "all", "alter", "analyze", "and", "as", "asc", "asensitive",
			// B开头的.
			"before", "between", "bigint", "binary", "blob", "both", "by",
			// C开头的.
			"call", "cascade", "case", "change", "char", "character", "check", "collate", "column", "condition", "connection", "constraint", "continue", "convert", "create", "cross", "current_date", "current_time", "current_timestamp", "current_user",
			"cursor",
			// D开头的.
			"database", "databases", "day_hour", "day_microsecond", "day_minute", "day_second", "dec", "decimal", "declare", "default", "delayed", "delete", "desc", "describe", "deterministic", "distinct", "distinctrow", "div", "double", "drop", "dual",
			// E开头的.
			"each", "else", "elseif", "enclosed", "escaped", "exists", "exit", "explain",
			// F开头的.
			"false", "fetch", "float", "float4", "float8", "for", "force", "foreign", "from", "fulltext",
			// G开头的.
			"goto", "grant", "group",
			// H开头的.
			"having", "high_priority", "hour_microsecond", "hour_minute", "hour_second",
			// I开头的.
			"if", "ignore", "in", "index", "infile", "inner", "inout", "insensitive", "insert", "int", "int1", "int2", "int3", "int4", "int8", "integer", "interval", "into", "is", "iterate",
			// J开头的.
			"join", "key", "keys", "kill",
			// L开头的.
			"label", "leading", "leave", "left", "like", "limit", "linear", "lines", "load", "localtime", "localtimestamp", "lock", "long", "longblob", "longtext", "loop", "low_priority",
			// M开头的.
			"match", "mediumblob", "mediumint", "mediumtext", "middleint", "minute_microsecond", "minute_second", "mod", "modifies",
			// N开头的.
			"natural", "not", "no_write_to_binlog", "null", "numeric",
			// O开头的.
			"on", "optimize", "option", "optionally", "or", "order", "out", "outer", "outfile",
			// P开头的.
			"precision", "primary", "procedure", "purge",
			// R开头的.
			"raid0", "range", "read", "reads", "real", "references", "regexp", "release", "rename", "repeat", "replace", "require", "restrict", "return", "revoke", "right", "rlike",
			// S开头的.
			"schema", "schemas", "second_microsecond", "select", "sensitive", "separator", "set", "show", "smallint", "spatial", "specific", "sql", "sqlexception", "sqlstate", "sqlwarning", "sql_big_result", "sql_calc_found_rows", "sql_small_result",
			"ssl", "starting", "straight_join",
			// T开头的.
			"table", "terminated", "then", "tinyblob", "tinyint", "tinytext", "to", "trailing", "trigger", "true",
			// U开头的.
			"undo", "union", "unique", "unlock", "unsigned", "update", "usage", "use", "using", "utc_date", "utc_time", "utc_timestamp",
			// V开头的.
			"values", "varbinary", "varchar", "varcharacter", "varying",
			// WXYZ开头的.
			"when", "where", "while", "with", "write", "x509", "xor", "year_month", "zerofill"));

	/**
	 * 判定指定名称是否为关键字.
	 * 
	 * @param name 指定名称
	 * @return 如果是关键字的返回true
	 */
	public static boolean isKeyword(String name) {
		return KEYWORD.contains(name);
	}
}