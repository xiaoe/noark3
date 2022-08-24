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
package xyz.noark.orm;

/**
 * 数据存储常理类.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.0
 */
public final class DataConstant {

    /**
     * 字符串字段最大宽度，10240~10K,这是一个折中值，并不是VarChar的最大长度
     * <p>
     * VARCHAR的最大长度=(最大行大小-NULL标识列占用字节数-长度标识字节数)/字符集单字符最大字节数,有余数时向下取整。 <br>
     * GBK：单字符最大可占用2个字节<br>
     * UTF8：单字符最大可占用3个字节<br>
     * UTF8MB4：单字符最大占4个字节<br>
     * https://learn.blog.csdn.net/article/details/103341778
     */
    public static final int VARCHAT_MAX_WIDTH = 10240;

    /**
     * 字段最大宽度，正常用在字符串上，超时此值时转化类型,<br>
     * 等于65535为Text~64kb，大于就是MEDIUMTEXT了~16M
     */
    public static final int TEXT_MAX_WIDTH = 65535;
    /**
     * 最大长度为65,535(216–1)字节的BLOB列。
     * 等于65535为Text~64kb，大于就是MEDIUMTEXT了~16M
     */
    public static final int BLOB_MAX_WIDTH = 65535;

    /**
     * 停止服务时存储数据最大等待时间，单位：分钟
     */
    public static final int SHUTDOWN_MAX_TIME = 10;

}
