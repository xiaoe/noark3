package xyz.noark.network.http;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import xyz.noark.core.util.CharsetUtils;
import xyz.noark.core.util.MapUtils;
import xyz.noark.core.util.StringUtils;
import xyz.noark.network.http.exception.UnrealizedMethodException;
import xyz.noark.network.util.ByteBufUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.Map.Entry;

/**
 * Noark实现的HTTP请求对象.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.4
 */
class NoarkHttpServletRequest implements HttpServletRequest {
    private final String uri;
    private final String method;
    private final String ip;
    /**
     * 请求参数Map
     */
    private Map<String, String[]> parameterMap = Collections.emptyMap();
    /**
     * 请求内容输入流
     */
    private InputStream requestBodyInputStream;

    NoarkHttpServletRequest(String uri, HttpMethod method, String ip) {
        this.uri = uri;
        this.method = method.name();
        this.ip = ip;
    }

    @Override
    public String getUri() {
        return uri;
    }

    @Override
    public String getMethod() {
        return method;
    }

    @Override
    public String getParameter(String name) {
        String[] array = this.getParameterValues(name);
        // 没有对应的值，那返回null
        if (array == null || array.length == 0) {
            return null;
        }
        // 如果有对应的参数，那默认给第一个值
        return array[0];
    }

    @Override
    public Enumeration<String> getParameterNames() {
        return Collections.enumeration(parameterMap.keySet());
    }

    @Override
    public String[] getParameterValues(String name) {
        return parameterMap.get(name);
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        return parameterMap;
    }

    @Override
    public InputStream getInputStream() {
        return requestBodyInputStream;
    }

    @Override
    public String getRemoteAddr() {
        return ip;
    }

    @Override
    public String toString() {
        return "HttpServletRequest [uri=" + uri + ", parameters=" + JSON.toJSONString(parameterMap) + "]";
    }

    /**
     * 解析请求中的参数.
     *
     * @param fhr     Netty的一次完成HTTP请求
     * @param decoder URI中参数解析器
     * @throws IOException 解析中可能会抛出IO异常
     */
    public void parse(FullHttpRequest fhr, QueryStringDecoder decoder) throws IOException {
        final HttpMethod method = fhr.method();
        final Map<String, List<String>> parameterMap = MapUtils.newHashMap(16);

        // GET请求
        if (HttpMethod.GET == method) {
            parseGetRequestParameter(decoder, parameterMap);
        }
        // POST请求
        else if (HttpMethod.POST == method) {
            parseGetRequestParameter(decoder, parameterMap);
            parsePostContent(fhr, parameterMap);
        }
        // 不支持其它方法，有需求再实现
        else {
            throw new UnrealizedMethodException(method.name(), decoder.path());
        }

        // 转化为Request的参数格式
        Map<String, String[]> result = MapUtils.newHashMap(parameterMap.size());
        for (Entry<String, List<String>> e : parameterMap.entrySet()) {
            result.put(e.getKey(), e.getValue().toArray(new String[0]));
        }
        this.parameterMap = result;
    }

    private void parsePostContent(FullHttpRequest fhr, Map<String, List<String>> parameterMap) throws IOException {
        // KEY的值，大小写无所谓，但Get出来的Value是大小写敏感的
        String contentType = fhr.headers().get("content-type");
        // 有些非正常的请求，可能会没有内容类型
        if (StringUtils.isEmpty(contentType)) {
            this.parsePostBodyContent(fhr);
        }
        // JSON类型的参数格式
        else if ("application/json".equalsIgnoreCase(contentType)) {
            this.parseJsonContent(fhr, parameterMap);
        }
        // 其他走HTTP常规参数编码key1=val1&key2=val2
        else {
            this.parsePostFromContent(fhr, parameterMap);
        }
    }

    private void parsePostBodyContent(FullHttpRequest fhr) {
        byte[] content = ByteBufUtils.readBytes(fhr.content());
        this.requestBodyInputStream = new ByteArrayInputStream(content);
    }

    private void parsePostFromContent(FullHttpRequest fhr, Map<String, List<String>> parameterMap) throws IOException {
        // 解析Post默认参数
        HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(fhr);
        try {
            List<InterfaceHttpData> parameterList = decoder.getBodyHttpDatas();
            for (InterfaceHttpData parameter : parameterList) {
                Attribute attr = (Attribute) parameter;
                parameterMap.computeIfAbsent(attr.getName(), key -> new ArrayList<>(1)).add(attr.getValue());
            }
        } finally {
            // This decoder will decode Body and can handle POST BODY.
            // You MUST call destroy() after completion to release all resources.
            decoder.destroy();
        }
    }

    private void parseJsonContent(FullHttpRequest fhr, Map<String, List<String>> parameterMap) {
        final ByteBuf content = fhr.content();
        if (content.readableBytes() == 0) {
            return;
        }

        JSONObject jsonObject = JSON.parseObject(content.toString(CharsetUtils.CHARSET_UTF_8));
        for (Map.Entry<String, Object> e : jsonObject.entrySet()) {
            parameterMap.computeIfAbsent(e.getKey(), key -> new ArrayList<>(1)).add(e.getValue().toString());
        }
    }

    private void parseGetRequestParameter(QueryStringDecoder decoder, Map<String, List<String>> parameterMap) {
        for (Map.Entry<String, List<String>> e : decoder.parameters().entrySet()) {
            parameterMap.computeIfAbsent(e.getKey(), key -> new ArrayList<>(e.getValue().size())).addAll(e.getValue());
        }
    }
}
