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
package xyz.noark.core.exception;

import xyz.noark.core.annotation.orm.Entity;

/**
 * 非法实体类异常.
 * <p>
 * 当实体类没有{@link Entity}注解时会抛出此异常.<br>
 * 当实体类没有可映射的属性时也会抛出此异常.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.0
 */
public class NoEntityException extends RuntimeException {
    private static final long serialVersionUID = -6578547540200771950L;

    /**
     * 构造一个非法实体类异常对象.
     *
     * @param className 实体类全名
     * @param message   异常产生原因描述
     */
    public NoEntityException(String className, String message) {
        super("非法的实体类：" + className + "，原因:" + message);
    }
}