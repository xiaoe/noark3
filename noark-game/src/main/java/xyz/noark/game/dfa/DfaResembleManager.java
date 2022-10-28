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

import xyz.noark.core.util.MapUtils;

import java.util.Map;

/**
 * DFA相似度的文字管理类.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.4
 */
public class DfaResembleManager {
    /**
     * 存放那些字母与数字相似的映射，只处理这些是打广告要用这些ID
     */
    private static final Map<Integer, Integer> resembleMap = MapUtils.newHashMap(64);

    static {
        // 数字类型
        addResembleWord('0', '⓪', '⓿', '〇', '零', '⁰', '₀');
        addResembleWord('1', '①', '➀', '➊', '❶', '⓵', '⒈', '一', '⑴', '㈠', '㊀', '壹', '¹', '₁', 'Ⅰ');
        addResembleWord('2', '②', '➁', '➋', '❷', '⓶', '⒉', '二', '⑵', '㈡', '㊁', '贰', '²', '₂', 'Ⅱ');
        addResembleWord('3', '③', '➂', '➌', '❸', '⓷', '⒊', '三', '⑶', '㈢', '㊂', '叁', '³', '₃', 'Ⅲ');
        addResembleWord('4', '④', '➃', '➍', '❹', '⓸', '⒋', '四', '⑷', '㈣', '㊃', '肆', '⁴', '₄', 'Ⅳ');
        addResembleWord('5', '⑤', '➄', '➎', '❺', '⓹', '⒌', '五', '⑸', '㈤', '㊄', '伍', '⁵', '₅', 'Ⅴ');
        addResembleWord('6', '⑥', '➅', '➏', '❻', '⓺', '⒍', '六', '⑹', '㈥', '㊅', '陆', '⁶', '₆', 'Ⅵ');
        addResembleWord('7', '⑦', '➆', '➐', '❼', '⓻', '⒎', '七', '⑺', '㈦', '㊆', '柒', '⁷', '₇', 'Ⅶ');
        addResembleWord('8', '⑧', '➇', '➑', '❽', '⓼', '⒏', '八', '⑻', '㈧', '㊇', '捌', '⁸', '₈', 'Ⅷ');
        addResembleWord('9', '⑨', '➈', '➒', '❾', '⓽', '⒐', '九', '⑼', '㈨', '㊈', '玖', '⁹', '₉', 'Ⅸ');

        // 字母类型
        addResembleWord('a', 'Ⓐ', 'ⓐ', '⒜', 'ₐ');
        addResembleWord('b', 'Ⓑ', 'ⓑ', '⒝');
        addResembleWord('c', 'Ⓒ', 'ⓒ', '⒞');
        addResembleWord('d', 'Ⓓ', 'ⓓ', '⒟');
        addResembleWord('e', 'Ⓔ', 'ⓔ', '⒠', 'ₑ');
        addResembleWord('f', 'Ⓕ', 'ⓕ', '⒡');
        addResembleWord('g', 'Ⓖ', 'ⓖ', '⒢');
        addResembleWord('h', 'Ⓗ', 'ⓗ', '⒣', 'ₕ');
        addResembleWord('i', 'Ⓘ', 'ⓘ', '⒤');
        addResembleWord('j', 'Ⓙ', 'ⓙ', '⒥');
        addResembleWord('k', 'Ⓚ', 'ⓚ', '⒦', 'ₖ');
        addResembleWord('l', 'Ⓛ', 'ⓛ', '⒧', 'ₗ');
        addResembleWord('m', 'Ⓜ', 'ⓜ', '⒨', 'ₘ');
        addResembleWord('n', 'Ⓝ', 'ⓝ', '⒩', 'ₙ');
        addResembleWord('o', 'Ⓞ', 'ⓞ', '⒪', 'ₒ');
        addResembleWord('p', 'Ⓟ', 'ⓟ', '⒫', 'ₚ');
        addResembleWord('q', 'Ⓠ', 'ⓠ', '⒬');
        addResembleWord('r', 'Ⓡ', 'ⓡ', '⒭');
        addResembleWord('s', 'Ⓢ', 'ⓢ', '⒮', 'ₛ');
        addResembleWord('t', 'Ⓣ', 'ⓣ', '⒯', 'ₜ');
        addResembleWord('u', 'Ⓤ', 'ⓤ', '⒰');
        addResembleWord('v', 'Ⓥ', 'ⓥ', '⒱');
        addResembleWord('w', 'Ⓦ', 'ⓦ', '⒲');
        addResembleWord('x', 'Ⓧ', 'ⓧ', '⒳', 'ₓ');
        addResembleWord('y', 'Ⓨ', 'ⓨ', '⒴');
        addResembleWord('z', 'Ⓩ', 'ⓩ', '⒵');
    }


    public static void addResembleWord(char src, char... resembleWordList) {
        Integer srcValue = (int) src;
        for (char key : resembleWordList) {
            resembleMap.put((int) key, srcValue);
        }
    }

    /**
     * 获取相似的替换字符
     *
     * @param src 要替换的字符
     * @return 返回此字符对应的原字符，如果没有则返回原本的字符
     */
    public static int getResembleWord(int src) {
        return resembleMap.getOrDefault(src, src);
    }

    public static void main(String[] args) {
        char[] str1 = "ⒶⒷⒸⒹⒺⒻⒼⒽⒾⒿⓀⓁⓂⓃⓄⓅⓆⓇⓈⓉⓊⓋⓌⓍⓎⓏ".toCharArray();
        char[] str2 = "ⓐⓑⓒⓓⓔⓕⓖⓗⓘⓙⓚⓛⓜⓝⓞⓟⓠⓡⓢⓣⓤⓥⓦⓧⓨⓩ".toCharArray();
        char[] str3 = "⒜⒝⒞⒟⒠⒡⒢⒣⒤⒥⒦⒧⒨⒩⒪⒫⒬⒭⒮⒯⒰⒱⒲⒳⒴⒵".toCharArray();

        int index = 0;
        for (int ii = 'a'; ii <= 'z'; ii++) {
            System.out.print("addResembleWord('" + (char) ii + "'");
            System.out.print(", '" + str1[index] + "'");
            System.out.print(", '" + str2[index] + "'");
            System.out.print(", '" + str3[index] + "'");

            System.out.print(");");
            System.out.println();
            index++;
        }
    }
}
