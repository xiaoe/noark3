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
package xyz.noark.game.dfa;

import xyz.noark.core.lang.ValidTime;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

/**
 * DFA敏感词库树上的节点.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.2
 */
class DfaNode {
    /**
     * 节点编码
     */
    private int value;
    /**
     * 子节点
     */
    private List<DfaNode> subNodes;
    /**
     * 是否是一个敏感词最后一位，默认false
     */
    private boolean last;
    /**
     * 敏感词生效时间
     */
    private ValidTime validTime;

    DfaNode(int value, boolean last) {
        this.value = value;
        this.last = last;
    }

    /**
     * 添加子节点，如果不存在就创建，存在直接返回此节点
     *
     * @param value 节点编码
     * @param last  是否是一个敏感词最后一位
     * @return 返回这个子节点
     */
    DfaNode addIfAbsent(final int value, final boolean last) {
        if (subNodes != null) {
            for (DfaNode subNode : subNodes) {
                if (subNode.value == value) {
                    if (!subNode.last && last) {
                        subNode.last = true;
                    }
                    return subNode;
                }
            }
        }
        return addSubNode(new DfaNode(value, last));
    }

    /**
     * 添加子节点
     *
     * @param subNode 子节点
     * @return 就是传入的子节点
     */
    private DfaNode addSubNode(final DfaNode subNode) {
        if (subNodes == null) {
            subNodes = new LinkedList<>();
        }
        subNodes.add(subNode);
        return subNode;
    }

    /**
     * 查找子节点
     *
     * @param value 节点编码
     * @return 如果存在节点编码就返回此子节点，如果没有就返回null
     */
    DfaNode querySub(final int value) {
        if (subNodes == null) {
            return null;
        }
        for (DfaNode subNode : subNodes) {
            if (subNode.value == value) {
                return subNode;
            }
        }
        return null;
    }

    /**
     * 判定当前节点是否为一个完整敏感词.
     *
     * @return 如果是一个完整的词就返回true
     */
    boolean isLast() {
        return last;
    }

    /**
     * 设置当前节点是否为一个完整敏感词.
     *
     * @param last 是否为一个完整敏感词
     */
    void setLast(boolean last) {
        this.last = last;
    }

    void setValidTime(ValidTime validTime) {
        this.validTime = validTime;
    }

    /**
     * 判定当前敏感词是否正在有效期间.
     *
     * @param now 指定时间
     * @return 如果在有效期间返回true
     */
    boolean isValid(LocalDateTime now) {
        return validTime == null || validTime.isValid(now);
    }
}