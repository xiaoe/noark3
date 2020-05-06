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
package xyz.noark.core.annotation.orm;

import java.lang.annotation.*;

/**
 * COLLATE注解用来指定排序规则
 * <p>
 * MYSQL查询默认是不区分大小写的，如果有需要的话，请使用此注解来修正创建语句<br>
 *
 * <pre>
 * &#64;Collate("utf8_bin")
 * private String uid;
 * </pre>
 *
 * <b>如果表已存在，这个属性并不能自动添加，需要修正长度来自动化表结构的变更</b>
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.3.6
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Collate {

    /**
     * Collate规则：
     * <p>
     * _bin: 表示的是binary case sensitive collation，也就是说是区分大小写的<br>
     * _cs: case sensitive collation，区分大小写<br>
     * _ci: case insensitive collation，不区分大小写<br>
     *
     * @return 指定编码（推荐"utf8_bin"）
     */
    String value();
}