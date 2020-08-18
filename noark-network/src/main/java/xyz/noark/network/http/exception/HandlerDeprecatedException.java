package xyz.noark.network.http.exception;

import xyz.noark.core.util.StringUtils;

/**
 * API已过时，主要用于废弃的功能.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.4
 */
public class HandlerDeprecatedException extends RuntimeException {
    private final String method;
    private final String uri;

    public HandlerDeprecatedException(String method, String uri) {
        super(StringUtils.join("No handler found for ", method, " ", uri));
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
