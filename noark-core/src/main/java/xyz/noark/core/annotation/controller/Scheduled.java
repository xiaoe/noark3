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
package xyz.noark.core.annotation.controller;

import xyz.noark.core.util.StringUtils;

import java.lang.annotation.*;

/**
 * Scheduled注解用来标识一个可延迟执行的方法.
 * <p>
 * 1. 延迟多久，间隔多久执行指定方法<br>
 * 2. CRON表达式的调度执行 <br>
 * 如果当前Controller线程组为玩家，当前标识的方法应该具有一个PlayerId的参数
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.2.6
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Scheduled {

    /**
     * 一种类似CRON的表达式。
     * <p>
     * 例cron="1/10 * * * * *" <br>
     * 如果设计表达式，下面两个参数将不在生效
     *
     * @return 可以解析为CRON调度的表达式
     */
    String cron() default StringUtils.EMPTY;

    /**
     * 首次延迟多长时间后再执行.
     *
     * @return 以毫秒为单位的周期
     */
    long initialDelay() default -1;

    /**
     * 以毫秒为单位的固定周期延迟执行，即执行开始时间为延迟开始时间.
     * <p>
     * 比如，5秒，10秒，15秒，20秒...
     *
     * @return 以毫秒为单位的周期
     */
    long fixedRate() default -1;
}