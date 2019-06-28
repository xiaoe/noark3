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
package com.company.game;

import com.alibaba.druid.pool.DruidDataSource;

import xyz.noark.core.annotation.Configuration;
import xyz.noark.core.annotation.Value;
import xyz.noark.core.annotation.configuration.Bean;
import xyz.noark.orm.accessor.DataAccessor;
import xyz.noark.orm.accessor.sql.mysql.MysqlDataAccessor;
import xyz.noark.orm.write.AsyncWriteService;
import xyz.noark.orm.write.impl.DefaultAsyncWriteServiceImpl;

/**
 * 启动配置文件.
 *
 * @since 3.2
 * @author 小流氓(176543888@qq.com)
 */
@Configuration
public class GameServerConfiguration {
	@Value("data.mysql.ip")
	private String mysqlIp;
	@Value("data.mysql.port")
	private int mysqlPort;
	@Value("data.mysql.db")
	private String mysqlDb;
	@Value("data.mysql.user")
	private String mysqlUser;
	@Value("data.mysql.password")
	private String mysqlPassword;

	@Bean
	public DataAccessor dataAccessor() {
		DruidDataSource dataSource = new DruidDataSource();
		dataSource.setDriverClassName("com.mysql.jdbc.Driver");
		dataSource.setUsername(mysqlUser);
		dataSource.setPassword(mysqlPassword);
		dataSource.setUrl(String.format("jdbc:mysql://%s:%d/%s?autoReconnect=true&useUnicode=true&characterEncoding=UTF-8&useSSL=false", mysqlIp, mysqlPort, mysqlDb));
		dataSource.setInitialSize(4);
		dataSource.setMinIdle(4);
		dataSource.setMaxActive(8);
		dataSource.setPoolPreparedStatements(false);

		MysqlDataAccessor accessor = new MysqlDataAccessor(dataSource);
		accessor.setStatementExecutableSqlLogEnable(true);
		accessor.setStatementParameterSetLogEnable(true);
		// 执行时间超过1秒的都要记录下.
		accessor.setSlowQuerySqlMillis(1000);
		return accessor;
	}

	@Bean
	public AsyncWriteService asyncWriteService() {
		return new DefaultAsyncWriteServiceImpl();
	}
}
