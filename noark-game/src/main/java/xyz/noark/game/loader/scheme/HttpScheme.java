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
package xyz.noark.game.loader.scheme;

import xyz.noark.core.exception.ServerBootstrapException;
import xyz.noark.core.util.HttpUtils;

import java.io.IOException;

/**
 * 密钥来源于HTTP.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.3.4
 */
class HttpScheme extends AbstractSecretkeyScheme {
    HttpScheme(int code) {
        super(code, loadSecretKey());
    }

    private static String loadSecretKey() {
        try {
            return HttpUtils.get("http://noark.xyz/secretkey/?id=9527");
        } catch (IOException e) {
            throw new ServerBootstrapException("加载密钥失败", e);
        }
    }
}