package xyz.noark.network.http.exception;

import xyz.noark.core.util.StringUtils;

/**
 * 404没有找到.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.4
 */
public class NoHandlerFoundException extends RuntimeException {
    private final String method;
    private final String uri;

    public NoHandlerFoundException(String method, String uri) {
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
