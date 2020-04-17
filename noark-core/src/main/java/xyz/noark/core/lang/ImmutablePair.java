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

/**
 * 一个不可变改变两个元素组成的抽象对象实现.
 *
 * @since 3.0
 * @author 小流氓[176543888@qq.com]
 */
public final class ImmutablePair<L, R> extends AbstractPair<L, R> {
	private static final long serialVersionUID = -4331739966766356742L;
	@SuppressWarnings("rawtypes")
	private static final ImmutablePair NULL = ImmutablePair.of(null, null);

	/**
	 * 返回一个双null组成的抽象对象.
	 * 
	 * @param <L> 左边元素的类型
	 * @param <R> 右边元素的类型
	 * @return 一个双null组成的抽象对象.
	 */
	@SuppressWarnings("unchecked")
	public static <L, R> ImmutablePair<L, R> nullPair() {
		return NULL;
	}

	/**
	 * 根据参数类型自动推断出一个不可变的抽象对象.
	 * 
	 * @param <L> 左边元素的类型
	 * @param <R> 右边元素的类型
	 * @param left 左边元素
	 * @param right 右边元素
	 * @return 一个不可变的抽象对象
	 */
	public static <L, R> ImmutablePair<L, R> of(final L left, final R right) {
		return new ImmutablePair<>(left, right);
	}

	/**
	 * 只有左参数时自动推断出一个不可变的抽象对象.
	 * 
	 * @param <L> 左边元素的类型
	 * @param <R> 右边元素的类型
	 * @param left 左边元素
	 * @return 一个不可变的抽象对象
	 */
	public static <L, R> ImmutablePair<L, R> ofLeft(final L left) {
		return new ImmutablePair<>(left, null);
	}

	/**
	 * 只有右参数时自动推断出一个不可变的抽象对象.
	 * 
	 * @param <L> 左边元素的类型
	 * @param <R> 右边元素的类型
	 * @param right 右边元素
	 * @return 一个不可变的抽象对象
	 */
	public static <L, R> ImmutablePair<L, R> ofRight(final R right) {
		return new ImmutablePair<>(null, right);
	}

	/** 左边元素 */
	private final L left;
	/** 右边元素 */
	private final R right;

	/**
	 * 创建一个抽象对象.
	 * 
	 * @param left 左边元素
	 * @param right 右边元素
	 */
	public ImmutablePair(final L left, final R right) {
		this.left = left;
		this.right = right;
	}

	@Override
	public L getLeft() {
		return left;
	}

	@Override
	public R getRight() {
		return right;
	}

	/**
	 * 不可变对象，是不可以设计初始值的.<br>
	 * {@link UnsupportedOperationException}
	 */
	@Override
	public R setValue(final R value) {
		throw new UnsupportedOperationException();
	}
}