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
package xyz.noark.core.util;

import xyz.noark.core.lang.TimeoutHashMap;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

/**
 * 锁工具类.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.1
 */
public final class LockUtils {
    /**
     * 5分钟的缓存时间
     */
    private static final int DURATION = 5;
    /**
     * 零长度的byte数组对象创建起来将比任何对象都经济<br>
     * 查看编译后的字节码：生成零长度的byte[]对象只需3条操作码，<br>
     * 而Object lock = new Object()则需要7行操作码
     */
    private static final TimeoutHashMap<Serializable, byte[]> LOCKER_STORE = new TimeoutHashMap<>(DURATION, TimeUnit.MINUTES, () -> new byte[0]);

    private LockUtils() {
    }

    /**
     * 获取个人锁.
     * <p>
     *
     * @param id 要锁的唯一ID.
     * @return 个人锁
     */
    public static Object getLock(Serializable id) {
        return LOCKER_STORE.get(id);
    }
}