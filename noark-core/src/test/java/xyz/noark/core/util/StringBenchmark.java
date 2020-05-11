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
