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

import java.lang.annotation.*;

/**
 * ThreadSafe注解表示这个类是线程安全的。
 * <p>
 * 当然了，这个并不代表他真的是线程安全的，要看他的具体实现，这个注解主要作用是传递给其他组件优化的一种手段。<br>
 * 比如日志记录，有一个参数对象，标识了这个注解，那在传递时这个对象不会被提前转化为String
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.3.9
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ThreadSafe {
}