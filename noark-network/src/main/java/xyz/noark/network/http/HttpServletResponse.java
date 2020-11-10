package xyz.noark.network.http;

/**
 * 一个HTTP的响应对象
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.4
 */
public interface HttpServletResponse {

    /**
     * 设计响应内容格式，如：application/json
     *
     * @param contentType 内容格式
     */
    void setContentType(String contentType);

    /**
     * 设计响应内容编码方式，如：UTF-8
     *
     * @param character 编码方式
     */
    void setCharacterEncoding(String character);

    /**
     * 设计响应编码
     *
     * @param status 响应编码
     */
    void setStatus(int status);

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
