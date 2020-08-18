package xyz.noark.network.http.exception;

import xyz.noark.core.util.StringUtils;

/**
 * 未实现的请求方式.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.4
 */
public class UnrealizedMethodException extends RuntimeException {
    private final String method;
    private final String uri;

    public UnrealizedMethodException(String method, String uri) {
        super(StringUtils.join("Unrealized method for ", method, " ", uri));
        this.method = method;
        this.uri = uri;
    }

    public String getMethod() {
        return this.method;
    }

    public String getUri() {
        return this.uri;
    }
}
