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
 * 一个可变改变两个元素组成的抽象对象实现.
 *
 * @since 3.0
 * @author 小流氓[176543888@qq.com]
 */
public final class MutablePair<L, R> extends AbstractPair<L, R> {
	private static final long serialVersionUID = 6743761112060610726L;

	/**
	 * 根据参数类型自动推断出一个不可变的抽象对象.
	 * 
	 * @param <L> 左边元素的类型
	 * @param <R> 右边元素的类型
	 * @param left 左边元素
	 * @param right 右边元素
	 * @return 一个不可变的抽象对象
	 */
	public static <L, R> MutablePair<L, R> of(final L left, final R right) {
		return new MutablePair<>(left, right);
	}

	/** 左边元素 */
	private L left;
	/** 右边元素 */
	private R right;

	/**
	 * 创建一个抽象对象.
	 * 
	 * @param left 左边元素
	 * @param right 右边元素
	 */
	public MutablePair(final L left, final R right) {
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
	 * 设置左边的值
	 * 
	 * @param left 左边的值
	 */
	public void setLeft(L left) {
		this.left = left;
	}

	/**
	 * 设置右边的值
	 * 
	 * @param right 右边的值
	 */
	public void setRight(R right) {
		this.right = right;
	}

	/**
	 * 不可变对象，是不可以设计初始值的.<br>
	 * {@link UnsupportedOperationException}
	 */
	@Override
	public R setValue(final R value) {
		final R result = getRight();
		setRight(value);
		return result;
	}
}