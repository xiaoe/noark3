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
package xyz.noark.log.message;

import xyz.noark.log.ThreadSafe;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

/**
 * 消息拼接辅助类.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.3.9
 */
class MessageHelper {
    private static final ThreadLocal<SimpleDateFormat> threadLocal = ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS"));

    private MessageHelper() {
    }

    /**
     * 预处理对象到字符串.
     * <p>
     * 进入日志线程前对一些有并发风险的对象进行提前处理.
     *
     * @param object 参数对象
     * @return 对象的ToString结果
     */
    static Object preprocessingEnteringLogThreadBefore(Object object) {
        if (object == null) {
            return null;
        }

        // 基本数据类型
        else if (object instanceof Number || object instanceof Date || object instanceof Character) {
            return object;
        }

        // 异常堆栈 或 标识线程安全
        else if (object instanceof Throwable || object.getClass().isAnnotationPresent(ThreadSafe.class)) {
            return object;
        }

        // 数组类型的输出...
        else if (object.getClass().isArray()) {
            if (object instanceof byte[]) {
                return (Arrays.toString((byte[]) object));
            } else if (object instanceof short[]) {
                return (Arrays.toString((short[]) object));
            } else if (object instanceof int[]) {
                return (Arrays.toString((int[]) object));
            } else if (object instanceof long[]) {
                return (Arrays.toString((long[]) object));
            } else if (object instanceof float[]) {
                return (Arrays.toString((float[]) object));
            } else if (object instanceof double[]) {
                return (Arrays.toString((double[]) object));
            } else if (object instanceof String[]) {
                return (Arrays.toString((String[]) object));
            } else {
                return (Arrays.toString((Object[]) object));
            }
        }

        // 不是基本数据类型且没有线程安全标识需要提前转化为String文本
        else {
            return object.toString();
        }
    }

    /**
     * 日志线程进行参数拼接工作.
     *
     * @param sb     拼接结果对象
     * @param object 参数对象
     */
    static void append(StringBuilder sb, Object object) {
        if (object == null) {
            sb.append((Object) null);
        }
        // 异常类型的输出...
        else if (object instanceof Throwable) {
            try (StringWriter sw = new StringWriter(); PrintWriter pw = new PrintWriter(sw)) {
                ((Throwable) object).printStackTrace(pw);
                sb.append("\n").append(sw);
            } catch (Exception e) {
                sb.append(object);
            }
        }
        // 时间类型的，需要格式化一下
        else if (object instanceof Date) {
            sb.append(threadLocal.get().format(object));
        }
        // 默认的交给StringBuilder
        else {
            sb.append(object);
        }
    }
}