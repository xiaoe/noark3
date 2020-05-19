package xyz.noark.network.http;

import com.alibaba.fastjson.JSON;
import xyz.noark.core.exception.UnrealizedException;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;

/**
 * @author 小流氓[176543888@qq.com]
 * @since 3.4
 */
public class NoarkHttpServletRequest implements HttpServletRequest {
    private final String uri;
    private final Map<String, String> parameters;
    private final String ip;

    public NoarkHttpServletRequest(String uri, Map<String, String> parameters, String ip) {
        this.uri = uri;
        this.parameters = parameters;
        this.ip = ip;
    }

    @Override
    public String getUri() {
        return uri;
    }

    @Override
    public String getParameter(String name) {
        return parameters.get(name);
    }

    @Override
    public Enumeration<String> getParameterNames() {
        return Collections.enumeration(parameters.keySet());
    }

    @Override
    public String[] getParameterValues(String name) {
        return new String[]{getParameter(name)};
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        throw new UnrealizedException("年后再实现");
    }

    @Override
    public String getRemoteAddr() {
        return ip;
    }

    @Override
    public String toString() {
        return "HttpServletRequest [uri=" + uri + ", parameters=" + JSON.toJSONString(parameters) + "]";
    }
}
