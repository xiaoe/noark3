package xyz.noark.network.http;

/**
 * @author 小流氓[176543888@qq.com]
 * @since 3.4
 */
public interface HttpServletResponse {

    void setContentType(String s);

    void setCharacterEncoding(String utf8);

    /**
     * 设计响应编码
     *
     * @param status 响应编码
     */
    void setStatus(int status);

    void send(int status, String msg);

    /**
     * 发送响应.
     *
     * @param msg 响应文本
     */
    void send(String msg);


    /**
     * 写入一个对象，序列化方式由contentType决定.
     *
     * @param str 需要写入的数据
     */
    void writeString(String str);

    /**
     * 写入一个对象，序列化方式由contentType决定.
     *
     * @param o 需要写入的数据
     */
    void writeObject(Object o);

    /**
     * 通知客户端
     */
    void flush();
}
