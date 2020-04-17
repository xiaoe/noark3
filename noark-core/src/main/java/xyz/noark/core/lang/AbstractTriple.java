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
package xyz.noark.core.lang;

import java.io.Serializable;
import java.util.Objects;

/**
 * 由三个元素组成的一个抽象对象.
 * <p>
 * 
 * @param <L> 左边元素的类型
 * @param <M> 中间元素的类型
 * @param <R> 右边元素的类型
 * @since 3.0
 * @author 小流氓[176543888@qq.com]
 */
public abstract class AbstractTriple<L, M, R> implements Serializable, Triple<L, M, R> {
	private static final long serialVersionUID = 5742943780888927005L;

	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj instanceof AbstractTriple<?, ?, ?>) {
			final AbstractTriple<?, ?, ?> other = (AbstractTriple<?, ?, ?>) obj;
			return Objects.equals(getLeft(), other.getLeft()) && Objects.equals(getMiddle(), other.getMiddle()) && Objects.equals(getRight(), other.getRight());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return (getLeft() == null ? 0 : getLeft().hashCode()) ^ (getMiddle() == null ? 0 : getMiddle().hashCode()) ^ (getRight() == null ? 0 : getRight().hashCode());
	}

	@Override
	public String toString() {
		return "(" + getLeft() + "," + getMiddle() + "," + getRight() + ")";
	}

	/**
	 * 自定义格式的ToString
	 * 
	 * @param format 格式化文本
	 * @return 返回格式化后的内容
	 */
	public String toString(final String format) {
		return String.format(format, getLeft(), getMiddle(), getRight());
	}
}