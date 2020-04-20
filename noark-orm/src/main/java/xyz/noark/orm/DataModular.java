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
package xyz.noark.orm;

import xyz.noark.core.Modular;
import xyz.noark.core.annotation.Autowired;
import xyz.noark.core.annotation.Component;
import xyz.noark.orm.write.AsyncWriteService;

/**
 * 数据存储模块.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.0
 */
@Component(name = Modular.DATA_MODULAR)
public class DataModular implements Modular {
    /**
     * 服务器数据存档间隔，单位：秒，默认值：5分钟
     */
    public static final String DATA_SAVE_INTERVAL = "data.save.interval";
    /**
     * 服务器数据缓存间隔，单位：秒，默认值：1小时
     */
    public static final String DATA_OFFLINE_INTERVAL = "data.offline.interval";
    /**
     * 服务器数据每次批量操作的最大数量，默认：256
     */
    public static final String DATA_BATCH_NUM = "data.batch.num";
    /**
     * 服务器数据存档SQL是否记录日志，默认值：false
     */
    public static final String DATA_SQL_LOG_ENABLE = "data.sql.log.enable";
    /**
     * 服务器数据存档SQL占位符参数是否记录日志（前提取决于{@link DataModular#DATA_SQL_LOG_ENABLE}）
     */
    public static final String DATA_SQL_LOG_PARAMETER_ENABLE = "data.sql.log.parameter.enable";
    /**
     * 服务器数据开启慢查询的时间（单位：ms）,默认为0，不开启
     */
    public static final String DATA_SLOW_QUERY_SQL_MILLIS = "data.slow.query.sql.millis";
    /**
     * 服务器数据是否智能删除表中多的字段，默认：false
     */
    public static final String DATA_AUTO_ALTER_TABLE_DROP_COLUMN = "data.auto.alter.table.drop.column";
    /**
     * 服务器数据是否智能修正文本字段的长度，默认：true
     */
    public static final String DATA_AUTO_ALTER_COLUMN_LENGTH = "data.auto.alter.column.length";
    /**
     * 服务器数据是否智能转化EMOJI的字段，默认：true
     */
    public static final String DATA_AUTO_ALTER_EMOJI_COLUMN = "data.auto.alter.emoji.column";

    /**
     * 数据存储默认开启下划线命名方式检测
     */
    public static boolean CheckUnderScoreCase = true;

    @Autowired
    private AsyncWriteService asyncWriteService;

    @Override
    public synchronized void init() {
        asyncWriteService.init();
    }

    @Override
    public void destroy() {
        asyncWriteService.shutdown();
    }
}