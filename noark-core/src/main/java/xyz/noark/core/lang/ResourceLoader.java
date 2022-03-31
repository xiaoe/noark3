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
package xyz.noark.core.lang;

import xyz.noark.core.util.StringUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * 资源加载.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.4
 */
public class ResourceLoader {
    protected static final String CLASSPATH_URL_PREFIX = "classpath:";

    protected InputStream newInputStream(String path, String zone, int step, String fileName) throws IOException {
        // ClassPath路径
        if (path.startsWith(CLASSPATH_URL_PREFIX)) {
            return newClasspathInputStream(path, zone, fileName);
        }

        // 配置存放目录
        Path dir = Paths.get(path, zone);
        for (int i = step; i > 1; i--) {
            Path resolve = dir.resolve(String.valueOf(i)).resolve(fileName);
            if (resolve.toFile().exists()) {
                return Files.newInputStream(resolve, StandardOpenOption.READ);
            }
        }
        
        // 没有前缀
        return Files.newInputStream(dir.resolve(fileName), StandardOpenOption.READ);
    }

    private InputStream newClasspathInputStream(String path, String zone, String fileName) {
        final StringBuilder sb = new StringBuilder(path.length() + fileName.length());

        // 先加上配置路径
        sb.append(path, CLASSPATH_URL_PREFIX.length(), path.length());
        this.buildSeparatorChar(sb);

        // 再处理地区目录
        if (StringUtils.isNotEmpty(zone)) {
            sb.append(zone);
            this.buildSeparatorChar(sb);
        }
        // 文件名称
        sb.append(fileName);

        // 使用ClassLoader来加载资源
        return Thread.currentThread().getContextClassLoader().getResourceAsStream(sb.toString());
    }

    private void buildSeparatorChar(StringBuilder sb) {
        if (sb.length() > 0 && sb.charAt(sb.length() - 1) != File.separatorChar) {
            sb.append(File.separatorChar);
        }
    }

    protected BufferedReader newBufferedReader(String path, String zone, int step, String fileName, Charset charset) throws IOException {
        InputStream is = this.newInputStream(path, zone, step, fileName);
        return new BufferedReader(new InputStreamReader(is, charset.newDecoder()));
    }
}