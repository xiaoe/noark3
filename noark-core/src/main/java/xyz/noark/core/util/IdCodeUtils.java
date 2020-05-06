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

/**
 * ID编号工具类.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.1
 */
public class IdCodeUtils {
    /**
     * 去除字符O，I,数字0,1
     */
    private final static char[] CHARS = {
            '2', '3', '4', '5', '6', '7', '8', '9',
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H',
            'J', 'K', 'L', 'M', 'N', 'P', 'Q', 'R',
            'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
    };
    private static final int CODE_CHARS_LENGTH = CHARS.length;
    private static final int[] NUMS = new int['Z' + 1];

    static {
        for (int i = 0; i < CHARS.length; i++) {
            NUMS[CHARS[i]] = i;
        }
    }

    /**
     * ID编码为短字符串，方便输入.
     *
     * @param id Long类型的数字
     * @return 短字符串
     */
    public static String toCode(long id) {
        char[] buf = new char[13];
        int index = buf.length - 1;
        while (id >= CODE_CHARS_LENGTH) {
            buf[index--] = CHARS[(int) (id % CODE_CHARS_LENGTH)];
            id >>>= 5;
        }
        buf[index] = CHARS[(int) (id)];
        return new String(buf, index, (buf.length - index));
    }

    /**
     * 短字符串解码为ID
     *
     * @param code 短字符串
     * @return Long类型的数字
     */
    public static long toLong(String code) {
        if (StringUtils.isEmpty(code)) {
            return 0;
        }
        long id = NUMS[code.charAt(0)];
        for (int i = 1; i < code.length(); i++) {
            id <<= 5;
            id |= NUMS[code.charAt(i)];
        }
        return id;
    }
}