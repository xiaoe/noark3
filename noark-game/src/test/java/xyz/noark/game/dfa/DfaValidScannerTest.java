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
package xyz.noark.game.dfa;

import org.junit.Before;
import org.junit.Test;
import xyz.noark.core.converter.impl.TimeRangeConverter;
import xyz.noark.core.lang.TimeRange;
import xyz.noark.core.lang.ValidTime;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * 带有有效时间的DFA算法测试
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.4
 */
public class DfaValidScannerTest {
    private DfaValidScanner scanner;

    @Before
    public void setUp() throws Exception {
        TimeRangeConverter converter = new TimeRangeConverter();
        TimeRange timeRange = converter.convert("[*][6][3-6][*][00:00-23:59:59:999]");
        List<DfaValidWord> sensitiveWordList = new ArrayList<>();
        // 2019.6.3新增屏蔽词：
        // 2019年6月3日起至2019年6月6日24点，需要单个屏蔽的敏感词：8、9、八、九、捌、玖、eight、nine、6、4、六、四、陆、肆、six、four
        sensitiveWordList.add(new DfaValidWordImpl("淘宝", null));
        sensitiveWordList.add(new DfaValidWordImpl("交易", null));
        sensitiveWordList.add(new DfaValidWordImpl("89", timeRange));
        sensitiveWordList.add(new DfaValidWordImpl("八", timeRange));
        sensitiveWordList.add(new DfaValidWordImpl("九", timeRange));
        sensitiveWordList.add(new DfaValidWordImpl("eight", timeRange));
        sensitiveWordList.add(new DfaValidWordImpl("nine", timeRange));
        sensitiveWordList.add(new DfaValidWordImpl("64", timeRange));
        sensitiveWordList.add(new DfaValidWordImpl("eightnine", timeRange));
        this.scanner = new DfaValidScanner(sensitiveWordList);
    }

    @Test
    public void testDfaValidScannerStringListOfDfaValidWord() {
        LocalDate today = LocalDate.now();
        boolean isJune = today.getMonthValue() == 6;
        boolean isDayIn36 = 3 <= today.getDayOfMonth() && today.getDayOfMonth() <= 6;
        if (isJune && isDayIn36) {
            assertTrue(scanner.contains("64"));
        } else {
            assertFalse(scanner.contains("64"));
        }
    }

    static class DfaValidWordImpl implements DfaValidWord {
        private final String text;
        private final ValidTime validTime;

        public DfaValidWordImpl(String text, ValidTime validTime) {
            this.text = text;
            this.validTime = validTime;
        }

        @Override
        public String text() {
            return text;
        }

        @Override
        public ValidTime validTime() {
            return validTime;
        }
    }
}
