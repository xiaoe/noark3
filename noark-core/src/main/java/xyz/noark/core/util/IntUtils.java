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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Int工具类，目前只是放一些常量，用于消除P3C的警告.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.4
 */
public class IntUtils {
    public static final int NUM_1 = 1;
    public static final int NUM_2 = 2;
    public static final int NUM_3 = 3;
    public static final int NUM_4 = 4;
    public static final int NUM_5 = 5;
    public static final int NUM_10 = 10;

    /**
     * 切割分组，给定一个数字，比如100，现在切成N份，随机一下，让切割效果看起来还不错的分法
     *
     * @param num   给定的一个数字
     * @param limit 分割的份数
     * @return 返回分割后的结果
     */
    public static List<Integer> split(int num, int limit) {
        List<Integer> result = new ArrayList<>(limit);
        // 前N-1次都是在一半+1中随机
        for (int i = 0; i < limit - 1; i++) {
            int random = RandomUtils.nextInt(0, num / 2 + 1);
            num -= random;
            result.add(random);
        }
        // 加上剩的
        result.add(num);
        // 洗牌，随出的结果会好看些
        Collections.shuffle(result);
        return result;
    }
}
