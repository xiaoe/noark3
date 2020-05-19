package xyz.noark.network.http;

/**
 * @author 小流氓[176543888@qq.com]
 * @since 3.4
 */
public interface HttpServletResponse {

    void setContentType(String s);

    void setCharacterEncoding(String utf8);

    void setStatus(int status);

    void send(int status, String msg);

    /**
     * 发送响应.
     *
     * @param msg 响应文本
     */
    void send(String msg);
}
