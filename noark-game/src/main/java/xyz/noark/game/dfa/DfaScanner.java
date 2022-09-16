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

import xyz.noark.core.util.StringUtils;

import java.util.List;

/**
 * 基于DFA算法构建的敏感词扫描器.
 * <p>
 * 实现目标：<br>
 * 1、大小写<br>
 * 2、全角半角<br>
 * 3、停顿词<br>
 * 4、重复词<br>
 * 5. 相似字符
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.2
 */
public class DfaScanner extends AbstractDfaScanner {

    /**
     * 构建一个敏感词扫描器.
     *
     * <pre>
     * 默认的分隔停顿符：`~!1@2#3$4%5^6&amp;7*8(9)0_-+={[}]|\\:;\&quot;'&lt;,&gt;.?/！￥%……｛｝【】abcdefghigklmnopqrstuvwxyz
     * </pre>
     *
     * @param maskWordList 屏蔽字库
     */
    public DfaScanner(List<String> maskWordList) {
        this(" `~!1@2#3$4%5^6&7*8(9)0_-+={[}]|\\:;\"'<,>.?/！￥%……｛｝【】abcdefghigklmnopqrstuvwxyz", maskWordList);
    }

    /**
     * 构建一个敏感词扫描器.
     *
     * @param symbols      分隔停顿符
     * @param maskWordList 屏蔽字库
     */
    public DfaScanner(String symbols, List<String> maskWordList) {
        this.initSeparatesSymbol(symbols);
        this.initMaskWordList(maskWordList);
    }

    /**
     * 初始化敏感词库
     *
     * @param maskWordList 敏感词列表
     */
    private void initMaskWordList(List<String> maskWordList) {
        for (String word : maskWordList) {
            if (StringUtils.isEmpty(word)) {
                continue;
            }
            this.addMaskWordList(word);
        }
    }
}