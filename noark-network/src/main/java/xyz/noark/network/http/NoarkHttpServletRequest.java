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
package xyz.noark.network.http;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import xyz.noark.core.util.CharsetUtils;
import xyz.noark.core.util.GzipUtils;
import xyz.noark.core.util.MapUtils;
import xyz.noark.core.util.StringUtils;
import xyz.noark.network.util.ByteBufUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
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
     * 请求头部的参数Map
     */
    private Map<String, String> headerMap;
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
    public String getHeader(String name) {
        return headerMap.get(name);
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
    void parse(FullHttpRequest fhr, QueryStringDecoder decoder) throws IOException {
        // 先解析出请求头中的参数
        this.parseHeaderMap(fhr);
        // 再处理常规参数
        this.parseParameterMap(fhr, decoder);
    }

    private void parseParameterMap(FullHttpRequest fhr, QueryStringDecoder decoder) throws IOException {
        final HttpMethod method = fhr.method();
        final Map<String, List<String>> parameterMap = MapUtils.newHashMap(16);
        // 1. 解析出URL中的参数
        this.parseUrlParameter(decoder, parameterMap);

        // 2. POST请求还有一个请求体的参数
        if (HttpMethod.POST == method) {
            this.parsePostContent(fhr, parameterMap);
        }

        // 转化为Request的参数格式
        Map<String, String[]> result = MapUtils.newHashMap(parameterMap.size());
        for (Entry<String, List<String>> e : parameterMap.entrySet()) {
            result.put(e.getKey(), e.getValue().toArray(new String[0]));
        }
        this.parameterMap = result;
    }

    private void parseHeaderMap(FullHttpRequest fhr) {
        final HttpHeaders headers = fhr.headers();
        this.headerMap = MapUtils.newHashMap(headers.size());
        Iterator<Entry<String, String>> it = headers.iteratorAsString();
        while (it.hasNext()) {
            Entry<String, String> next = it.next();
            headerMap.put(next.getKey(), next.getValue());
        }
    }

    private void parsePostContent(FullHttpRequest fhr, Map<String, List<String>> parameterMap) throws IOException {
        HttpHeaders headers = fhr.headers();

        // KEY的值，大小写无所谓，但Get出来的Value是大小写敏感的
        String contentType = headers.get(HttpHeaderNames.CONTENT_TYPE);
        String charset = CharsetUtils.UTF_8;
        String contentEncoding = headers.get(HttpHeaderNames.CONTENT_ENCODING);

        // application/json;charset=utf-8
        if (StringUtils.isNotEmpty(contentType)) {
            String[] array = contentType.split(";", 2);
            if (array.length == 2) {
                contentType = array[0].trim();
                // 附加参数
                String[] parameterArray = array[1].split("=");
                for (int i = 0; i < parameterArray.length; i = i + 2) {
                    if ("charset".equalsIgnoreCase(parameterArray[i].trim())) {
                        charset = parameterArray[i + 1].trim();
                    }
                }
            }
        }

        // 有些非正常的请求，可能会没有内容类型
        if (StringUtils.isEmpty(contentType)) {
            this.parsePostBodyContent(fhr, contentEncoding);
        }
        // JSON类型的参数格式
        else if ("application/json".equalsIgnoreCase(contentType)) {
            this.parsePostJsonContent(fhr, parameterMap, contentEncoding, charset);
        }
        // 其他走HTTP常规参数编码key1=val1&key2=val2
        else {
            this.parsePostFromContent(fhr, parameterMap);
        }
    }

    private void parsePostBodyContent(FullHttpRequest fhr, String contentEncoding) throws IOException {
        byte[] content = ByteBufUtils.readBytes(fhr.content());
        // Gzip压缩
        if (GzipUtils.ENCODING_GZIP.equalsIgnoreCase(contentEncoding)) {
            content = GzipUtils.uncompress(content);
        }
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

    private void parsePostJsonContent(FullHttpRequest fhr, Map<String, List<String>> parameterMap, String contentEncoding, String charset) throws IOException {
        final ByteBuf byteBuf = fhr.content();
        if (byteBuf.readableBytes() == 0) {
            return;
        }

        JSONObject jsonObject;
        // Gzip压缩
        if (GzipUtils.ENCODING_GZIP.equalsIgnoreCase(contentEncoding)) {
            byte[] content = ByteBufUtils.readBytes(fhr.content());
            content = GzipUtils.uncompress(content);
            jsonObject = JSON.parseObject(new String(content, charset));
        }
        // 没有压缩
        else {
            jsonObject = JSON.parseObject(byteBuf.toString(Charset.forName(charset)));
        }

        for (Map.Entry<String, Object> e : jsonObject.entrySet()) {
            parameterMap.computeIfAbsent(e.getKey(), key -> new ArrayList<>(1)).add(e.getValue().toString());
        }
    }

    private void parseUrlParameter(QueryStringDecoder decoder, Map<String, List<String>> parameterMap) {
        for (Map.Entry<String, List<String>> e : decoder.parameters().entrySet()) {
            parameterMap.computeIfAbsent(e.getKey(), key -> new ArrayList<>(e.getValue().size())).addAll(e.getValue());
        }
    }
}
