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
package xyz.noark.game.id;

import xyz.noark.core.exception.ServerBootstrapException;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * ID生成策略.
 * <p>
 * 需要持久化：角色ID，道具ID等...<br>
 *
 * <p>
 * 持久化：区服编号（16位）+ 计次（18位）+ 自旋因子（25位）+ 类型（1位） <br>
 * <p>
 * 使用方案：创建一个ID生成工厂类，调用此实例.<br>
 * 需要捕获IdMaxSequenceException，超出一轮计次+1存档后再次使用
 * <pre>
 *    public synchronized static long generateId() {
 *      try {
 *          return generator.generateId();
 *      } catch (IdMaxSequenceException e) {
 *          logger.warn("ID生成工厂一轮用完了....");
 *          serverInfoService.updateTimes();
 *          return generateId();
 *      }
 *    }
 * </pre>
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.1
 */
public class IdGenerator {
    private static final long MAX_TIMES = 1L << 18;
    private static final long MAX_SEQUENCE = 1L << 25;
    /**
     * 区服编号
     */
    private final long sid;
    /**
     * 重启次数:服务器每重启一次计次加1.最大值为‭26_2143
     */
    private final long times;
    /**
     * 自旋因子:每次加1，最大值为‭3355_4431‬
     */
    private final AtomicInteger sequence = new AtomicInteger(0);

    /**
     * 构建一个ID生成器.
     *
     * @param sid   区服编号
     * @param times 重启次数
     */
    public IdGenerator(int sid, int times) {
        this.sid = sid;
        this.times = times;
        // 区服启动已达最大次数了.
        if (times >= MAX_TIMES) {
            throw new ServerBootstrapException("区服启动已达最大次数了,也应该合服了吧，向后合噢... times=" + times);
        }
    }

    /**
     * 生成一个持久化类型的ID.
     * <p>
     * 持久化：区服编号（16位）+ 计次（18位）+ 自旋因子（25位）+ 类型（1位） <br>
     *
     * @return 唯一ID
     * @throws IdMaxSequenceException 如果最大自旋因子用完了就会抛出此异常，调用者必需计次加一重新来过
     */
    public long generateId() throws IdMaxSequenceException {
        return sid << 44 | times << 26 | nextSequence() << 1 | 1;
    }

    /**
     * 生成下一个自增因子.
     *
     * @return
     * @throws IdMaxSequenceException 如果自增因子已达最大值时会抛出此异常...
     */
    private long nextSequence() throws IdMaxSequenceException {
        final long result = sequence.incrementAndGet();
        // 需要判定，当自旋因子达到上限时，调整启动次
        if (result >= MAX_SEQUENCE) {
            throw new IdMaxSequenceException("自增因子已达最大值,包装生成器要自增启动次数啦... sequence=" + result);
        }
        return result;
    }

    @Override
    public String toString() {
        return "IdGenerator [sid=" + sid + ", times=" + times + ", sequence=" + sequence + "]";
    }
}