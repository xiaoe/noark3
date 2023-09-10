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
package xyz.noark.network.http.intercept;

import io.netty.handler.codec.http.HttpResponseStatus;
import xyz.noark.core.annotation.controller.IpAllowList;
import xyz.noark.core.env.EnvConfigHolder;
import xyz.noark.core.network.HandlerMethod;
import xyz.noark.core.util.IpUtils;
import xyz.noark.core.util.StringUtils;
import xyz.noark.network.http.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * IP访问权限拦截器.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.4
 */
public class IpIntercept extends HandlerInterceptorAdapter {
    private final Map<String, IpAllowListConfig> cache = new ConcurrentHashMap<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, HandlerMethod handler) {
        final String ip = request.getRemoteAddr();

        IpAllowList allowList = handler.getAnnotation(IpAllowList.class);
        // 没有任何声明，那就认为只能内网访问.
        if (allowList == null) {
            // 不是内网IP，结束后续
            if (!IpUtils.isInnerIp(ip)) {
                return notAccess(response);
            }
        }
        // 有声明，那就要按规则判定
        else {
            String value = allowList.value();

            // 直接写了个*，那就放过，不拦截了...
            if (StringUtils.ASTERISK.equals(value)) {
                return true;
            }

            IpAllowListConfig config = cache.computeIfAbsent(value, this::createIpWhiterListConfig);
            if (config.notAccess(ip)) {
                return notAccess(response);
            }
        }

        // 通过
        return true;
    }

    private boolean notAccess(HttpServletResponse response) {
        // 不能访问，给个提示
        response.setStatus(HttpResponseStatus.UNAUTHORIZED.code());
        response.writeObject(new HttpResult(HttpErrorCode.NOT_AUTHORIZED, "request's API not authorized."));
        return false;
    }

    private IpAllowListConfig createIpWhiterListConfig(String key) {
        return new IpAllowListConfig(EnvConfigHolder.getString(key));
    }
}
