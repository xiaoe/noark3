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
package xyz.noark.game.template;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import xyz.noark.core.exception.ServerBootstrapException;
import xyz.noark.core.lang.PairHashMap;
import xyz.noark.core.lang.PairMap;
import xyz.noark.core.lang.TripleHashMap;
import xyz.noark.core.lang.TripleMap;
import xyz.noark.core.util.StringUtils;

/**
 * 抽象实现的模板加载器.
 *
 * @since 3.0
 * @author 小流氓(176543888@qq.com)
 */
public abstract class AbstractTemplateLoader implements TemplateLoader {
	/** 策划配置文件路径 */
	protected final String templatePath;
	/**
	 * 版本大区，比如CN，US，KR...<br>
	 * 如果为""则没有版本目录，直接索引{@link AbstractTemplateLoader#templatePath}
	 */
	protected final String zone;

	public AbstractTemplateLoader(String templatePath) {
		this(templatePath, StringUtils.EMPTY);
	}

	public AbstractTemplateLoader(String templatePath, String zone) {
		this.templatePath = templatePath;
		this.zone = zone;
		if (zone == null) {
			throw new ServerBootstrapException("版本目录配置不可以为null,如果没有版本规划请使用\"\"");
		}
	}

	@Override
	public <K, T> Map<K, T> loadAll(Class<T> klass, Function<? super T, ? extends K> keyMapper) {
		return this.loadAll(klass).stream().collect(Collectors.toMap(keyMapper, Function.identity()));
	}

	@Override
	public <L, R, T> PairMap<L, R, T> loadAll(Class<T> klass, Function<? super T, ? extends L> leftMapper, Function<? super T, ? extends R> rightMapper) {
		return new PairHashMap<>(loadAll(klass), leftMapper, rightMapper);
	}

	@Override
	public <L, M, R, T> TripleMap<L, M, R, T> loadAll(Class<T> klass, Function<? super T, ? extends L> leftMapper, Function<? super T, ? extends M> middleMapper, Function<? super T, ? extends R> rightMapper) {
		return new TripleHashMap<>(loadAll(klass), leftMapper, middleMapper, rightMapper);
	}
}