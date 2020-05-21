package xyz.noark.network.http.intercept;

import xyz.noark.core.annotation.controller.IpWhiteList;
import xyz.noark.core.env.EnvConfigHolder;
import xyz.noark.core.network.HandlerMethod;
import xyz.noark.core.util.IpUtils;
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
    private final Map<String, IpWhiterListConfig> cache = new ConcurrentHashMap<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, HandlerMethod handler) {
        final String ip = request.getRemoteAddr();

        IpWhiteList whiteList = handler.getAnnotation(IpWhiteList.class);
        // 没有任何声明，那就认为只能内网访问.
        if (whiteList == null) {
            // 不是内网IP，结束后续
            if (!IpUtils.isInnerIp(ip)) {
                return notAccess(response);
            }
        }
        // 有声明，那就要按规则判定
        else {
            IpWhiterListConfig config = cache.computeIfAbsent(whiteList.value(), this::createIpWhiterListConfig);
            if (config.notAccess(ip)) {
                return notAccess(response);
            }
        }

        // 通过
        return true;
    }

    private boolean notAccess(HttpServletResponse response) {
        // 不能访问，给个提示
        response.writeObject(new HttpResult(HttpErrorCode.NOT_AUTHORIZED, "request's API not authorized."));
        return false;
    }

    private IpWhiterListConfig createIpWhiterListConfig(String key) {
        return new IpWhiterListConfig(EnvConfigHolder.getString(key));
    }
}
