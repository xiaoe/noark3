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
package xyz.noark.core.util;

import xyz.noark.benchmark.Benchmark;

import java.util.StringJoiner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 字符串相关测试数据
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.4
 */
public class StringBenchmark {

    private static final Benchmark benchmark = new Benchmark(100_0000);

    public static void main(String[] args) throws Exception {
        benchmark.doSomething("Jdk:", () -> StringUtils.split("127.0.0.1", "."));
        benchmark.doSomething("Noark:", () -> testSplitByJdk("127.0.0.1", "\\."));

        long value = Long.MIN_VALUE;
        benchmark.doSomething("OKIO的方案:", () -> StringUtils.asciiSizeInBytes(value));
        benchmark.doSomething("成龙的方案:", () -> String.valueOf(value).length());

        String[] strings = new String[]{"aaaaaaaaaaaaaaaaaaa", "bbbbbbbbbbbbbbbbbbbbbbbbb"};
        benchmark.doSomething("join:", () -> StringUtils.build(",", "{", "}", strings));
        benchmark.doSomething("join1:", () -> join1(",", "{", "}", strings));
        benchmark.doSomething("join2:", () -> join2(",", "{", "}", strings));
    }

    private static String join1(String delimiter, String prefix, String suffix, String... strings) {
        StringJoiner result = new StringJoiner(delimiter, prefix, suffix);
        for (String str : strings) {
            result.add(str);
        }
        return result.toString();
    }

    private static String join2(String delimiter, String prefix, String suffix, String... strings) {
        return Stream.of(strings).collect(Collectors.joining(delimiter, prefix, suffix));
    }

    private static String[] testSplitByJdk(String ip, String regex) {
        return ip.split(regex);
    }
}
