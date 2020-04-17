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

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 如果使用@ResponseBody注释的方法，Noark会将返回值以特定的格式写入到response的body区域。
 * <p>
 * 0. 当方法上面有写@ResponseBody时，那就直接以特定的格式写入到response的body区域<br>
 * 1. 当方法上面没有写@ResponseBody时<br>
 * 如果返回值是HttpResult类或子类的话，那就直接以特定的格式写入到response的body区域<br>
 * 如果返回值不是HttpResult类或子类的话，底层会将方法的返回值封装为HttpResult对象里的data属性，然后再以特定的格式写入到response的body区域<br>
 * 
 * @since 3.4
 * @author 小流氓[176543888@qq.com]
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ResponseBody {}