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
package xyz.noark.log;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.UUID;

import static xyz.noark.log.LogHelper.logger;

/**
 * 日志测试类.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.0
 */
public class LogTest {
    @Before
    public void setUp() throws Exception {
        HashMap<String, String> config = new HashMap<>();
        config.put("log.console", "false");
        config.put("log.layout.pattern", "%date{yyyy-MM-dd HH:mm:ss.SSS} %level [%thread]<traceId=%X{traceId}>[%file:%line] - %msg%n");
        LogManager.init(config);
    }

    @After
    public void tearDown() throws Exception {
        LogManager.shutdown();
    }

    @Test
    public void test() {
        MDC.put("traceId", UUID.randomUUID());
        logger.debug("haha{}", 123, "abc");
        logger.info("haha");
        logger.warn("123123123, {},{}", 1L, null);
        logger.error("123123123", new RuntimeException("123"));
        logger.debug("boolean={}", true);
        logger.debug("array={}", 1, 2, 3);
        logger.debug("array={}", new byte[]{1, 2});
        logger.debug("array={}", new int[]{1, 2});
        MDC.clear();
    }
}