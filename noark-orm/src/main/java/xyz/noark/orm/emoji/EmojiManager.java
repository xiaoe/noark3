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
package xyz.noark.orm.emoji;

import java.util.HashMap;
import java.util.Map;

/**
 * Emoji管理器.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.4
 */
public class EmojiManager {
    /**
     * Emoji树，用于Emoji转化为别名查询
     */
    private static final Map<Character, EmojiNode> EMOJI_ROOT = new HashMap<>(256);
    /**
     * 所有Emoji配置，别名--配置
     */
    private static final Map<String, EmojiConfig> EMOJI_CONFIG_MAP = new HashMap<>(2560);

    static {
        for (EmojiConfig config : EmojiLoader.loadAll(EmojiConfig.class)) {
            // 初始化别名对应配置的映射关系
            config.getAliases().forEach(v -> EMOJI_CONFIG_MAP.put(v, config));
            // 初始化Emoji树
            initEmojiConfig(config);
        }
        // initialCapacity = (需要存储的元素个数 / 负载因子) + 1
        // System.out.println(emojiRoot.size() / 0.75 + 1);
        // System.out.println(emojiConfigMap.size() / 0.75 + 1);
    }

    private static void initEmojiConfig(EmojiConfig config) {
        final char[] array = config.getEmoji().toCharArray();
        EmojiNode fnode = EMOJI_ROOT.computeIfAbsent(array[0], key -> new EmojiNode());
        for (int i = 1, len = array.length; i < len; i++) {
            fnode = fnode.addIfAbsent(array[i]);
        }
        fnode.setConfig(config);
    }

    /**
     * 解析输入文本中的Emoji为别名显示内容
     *
     * @param text 输入文本
     * @return 解析替换后的文本内容
     */
    public static String parseToAliases(final String text) {
        // 输入为空，返回也为空
        if (text == null) {
            return null;
        }
        // 逐字符匹配
        StringBuilder sb = null;
        for (int i = 0, length = text.length(); i < length; i++) {
            final char cur = text.charAt(i);
            EmojiNode node = EMOJI_ROOT.get(cur);
            if (node == null) {
                // 如果已初始化，那就要收集所以字符
                if (sb != null) {
                    sb.append(cur);
                }
                continue;
            }

            // 当前检查字符的备份
            EmojiNode backupsNode = node;
            boolean mark = false;
            int markIndex = -1;

            // 单节点匹配
            if (node.hasEmoji()) {
                mark = true;
                markIndex = i;
            }

            for (int k = i + 1; k < length; k++) {
                // 查询子节点
                char temp = text.charAt(k);
                node = backupsNode.querySub(temp);
                if (node == null) {
                    break;
                }

                backupsNode = node;
                markIndex = k;

                // 匹配最长，不跳出此轮循环
                if (node.hasEmoji()) {
                    mark = true;
                }
            }

            // 发现Emoji表情
            if (mark) {
                // 初始化StringBuilder
                if (sb == null) {
                    sb = new StringBuilder(length + 256);
                    if (i > 0) {
                        sb.append(text.substring(0, i));
                    }
                }
                // 替换为别名...
                sb.append(':').append(backupsNode.getAliases()).append(':');
                i = markIndex;
            }
            // 如果SB已初始化，那后面不是表情也要添加进去
            else if (sb != null) {
                sb.append(cur);
            }
        }
        // 如果没有替换操作，那就返回原字符串
        return sb == null ? text : sb.toString();
    }

    public static String parseToUnicode(String text) {
        // 输入为空，返回也为空
        if (text == null) {
            return null;
        }
        // 逐字符匹配
        StringBuilder sb = null;
        int startIndex = -1;
        for (int i = 0, length = text.length(); i < length; i++) {
            final char cur = text.charAt(i);
            // 如果SB已初始化，那后面不是表情也要添加进去
            if (sb != null) {
                sb.append(cur);
            }
            // 发现冒号
            if (cur == ':') {
                // 如果没有开始，那就算他开始
                if (startIndex == -1) {
                    startIndex = i;
                    continue;
                }
                // 已经开始，那就要算结束啦
                else {
                    String aliases = text.substring(startIndex + 1, i);
                    EmojiConfig config = EMOJI_CONFIG_MAP.get(aliases);
                    // 不存在，则以当前位置为开始点继续扫描
                    if (config == null) {
                        startIndex = i;
                    }
                    // 如果存在Emoji表情
                    else {
                        if (sb == null) {
                            sb = new StringBuilder(length);
                            if (startIndex > 0) {
                                sb.append(text.substring(0, startIndex));
                            }
                        } else {
                            sb.setLength(sb.length() - aliases.length() - 2);
                        }
                        sb.append(config.getEmoji());
                        startIndex = -1;
                    }
                }
            }
            // 优化JSON快速失败
            else if (startIndex >= 0) {
                if (cur == ',' || cur == '"') {
                    startIndex = -1;
                }
            }
        }
        // 如果没有替换操作，那就返回原字符串
        return sb == null ? text : sb.toString();
    }
}