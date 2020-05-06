/*
 * Copyright © 2018 www.noark.xyz All Rights Reserved.
 *
 * 感谢您选择Noark框架，希望我们的努力能为您提供一个简单、易用、稳定的服务器端框架 ！
 * 除非符合Noark许可协议，否则不得使用该文件，您可以下载许可协议文件：
 *
 * 		http://www.noark.xyz/LICENSE
 *
 * 1.未经许可，任何公司及个人不得以任何方式或理由对本框架进行修改、使用和传播;
 * 2.禁止在本项目或任何子项目的基础上发展任何派生版本、修改版本或第三方版本;
 * 3.无论你对源代码做出任何修改和改进，版权都归Noark研发团队所有，我们保留所有权利;
 * 4.凡侵犯Noark版权等知识产权的，必依法追究其法律责任，特此郑重法律声明！
 */
package xyz.noark.core.util;

import java.util.Stack;

/**
 * HTML工具类.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.4
 */
public class HtmlUtils {

    /**
     * 将标准的HTML转化为Unity能识别HTML.
     * <p>
     * 其实只处理加粗、斜体、大小、颜色4个标签，其他标签默认删除
     *
     * @param html 标准HTML文本
     * @return Unity能识别HTML
     */
    public static String toUnityHtml(final String html) {
        final Stack<HtmlTag> stack = new Stack<>();
        final StringBuilder sb = new StringBuilder(html.length());
        for (int i = 0, len = html.length(); i < len; i++) {
            char cur = html.charAt(i);
            if (cur == '<' && len > i + 1) {
                HtmlTag tag = readTag(html, i);
                i = tag.getLength();
                // 结束标签
                if (tag.isEnd()) {
                    if (!stack.isEmpty() && stack.peek().getName().equals(tag.getName())) {
                        tag = stack.pop();
                    }
                    tag.html(sb, true);
                }
                // 开始标签
                else {
                    stack.push(tag);
                    tag.html(sb, false);
                }
            } else {
                sb.append(cur);
            }
        }
        return sb.toString();
    }

    private static HtmlTag readTag(final String html, int index) {
        final HtmlTag tag = new HtmlTag();

        StringBuilder sb = new StringBuilder(16);
        boolean color = false;
        boolean size = false;
        for (int i = index + 1, len = html.length(); i < len; i++) {
            char cur = html.charAt(i);
            if (cur == '/') {
                tag.setEnd(true);
                continue;
            } else if (cur == ' ') {
                tag.setName(sb.toString());
                sb.setLength(0);
            } else if (cur == '>') {
                if (tag.getName() == null) {
                    tag.setName(sb.toString());
                }
                tag.setLength(i);
                break;
            } else if (cur == '"') {
                if (color) {
                    tag.setColor(sb.toString());
                    color = false;
                } else if (size) {
                    tag.setSize(sb.toString().replace("px", ""));
                    size = false;
                }
                sb.setLength(0);
            } else if (cur == ':') {
                if ("color".equals(sb.toString())) {
                    color = true;
                } else if ("font-size".equals(sb.toString())) {
                    size = true;
                }
                sb.setLength(0);
            } else if (cur == ';') {
                if (color) {
                    tag.setColor(sb.toString());
                    color = false;
                } else if (size) {
                    tag.setSize(sb.toString().replace("px", ""));
                    size = false;
                }
                sb.setLength(0);
            } else {
                sb.append(cur);
            }
        }
        return tag;
    }

    /**
     * HTML标签.
     *
     * @author 小流氓[176543888@qq.com]
     * @since 3.4
     */
    static class HtmlTag {
        private String name;
        private boolean end;
        private int length;
        private String color;
        private String size;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public boolean isEnd() {
            return end;
        }

        public void setEnd(boolean end) {
            this.end = end;
        }

        public int getLength() {
            return length;
        }

        public void setLength(int length) {
            this.length = length;
        }

        public void setColor(String color) {
            this.color = color;
        }

        public void setSize(String size) {
            this.size = size;
        }

        public void html(StringBuilder sb, boolean isEnd) {
            switch (name) {
                case "b":
                case "strong":
                    sb.append(isEnd ? "</b>" : "<b>");
                    break;
                case "i":
                case "em":
                    // 斜体
                    // <i>usually</i>
                    sb.append(isEnd ? "</i>" : "<i>");
                    break;
                case "span":
                    // 大小
                    // <size=50>largely</size>
                    if (size != null) {
                        sb.append(isEnd ? "</size>" : "<size=" + size + ">");
                    }
                    // 颜色
                    // <color=#00ffffff>cccc</color>
                    if (color != null) {
                        sb.append(isEnd ? "</color>" : "<color=" + color + ">");
                    }
                    break;
                case "br":
                    sb.append(isEnd ? "<br>" : "");
                    break;
                default:
                    break;
            }
        }

        @Override
        public String toString() {
            return "HtmlTag [name=" + name + ", end=" + end + ", length=" + length + ", color=" + color + ", size=" + size + "]";
        }
    }
}