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
package xyz.noark.network.http;

/**
 * HTTP错误编码.
 * <p>
 * ret = 0: 正确返回 <br>
 * ret &gt; 0: 调用OpenAPI时发生错误，需要开发者进行相应的处理。 <br>
 * -50 &lt;= ret &lt;= -1: 接口调用不能通过接口代理机校验，需要开发者进行相应的处理。<br>
 * ret &lt;-50: 系统内部错误，请通过企业QQ联系技术支持，调查问题原因并获得解决方案。
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.0
 */
public class HttpErrorCode {
    /**
     * 正确返回
     */
    public static final int OK = 0;
    /**
     * 非法参数.
     */
    public static final int PARAMETERS_INVALID = -1;
    /**
     * IP没有权限访问，请添加白名单.
     */
    public static final int NOT_AUTHORIZED = -2;
    /**
     * 签名失败.
     */
    public static final int SIGN_FAILED = -3;
    /**
     * API不存在。
     */
    public static final int NO_API = -4;
    /**
     * 服务器内部错误.
     */
    public static final int INTERNAL_ERROR = -5;
    /**
     * API已废弃
     */
    public static final int API_DEPRECATED = -6;
}