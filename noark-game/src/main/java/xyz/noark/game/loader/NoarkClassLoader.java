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
package xyz.noark.game.loader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import xyz.noark.core.util.ByteArrayUtils;
import xyz.noark.game.loader.scheme.SchemeManager;

/**
 * Noark类加载器.
 *
 * @since 3.3.4
 * @author 小流氓[176543888@qq.com]
 */
public class NoarkClassLoader extends ClassLoader {
	static {
		ClassLoader.registerAsParallelCapable();
	}

	public NoarkClassLoader(ClassLoader parent) {
		super(parent);
	}

	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		try {
			return super.findClass(name);
		}
		// 如果出现解不开的Class，那就尝试分析这个文件，然后进行2次载入
		catch (ClassFormatError e) {
			return this.analysisFormatErrorClass(name);
		}
	}

	private Class<?> analysisFormatErrorClass(String name) throws ClassNotFoundException {
		byte[] bytes = loadBytesForClassName(name);
		int scheme = ByteArrayUtils.toInt(bytes);
		SchemeManager.getScheme(scheme).decode(bytes);
		return defineClass(name, bytes, 4, bytes.length);
	}

	protected byte[] loadBytesForClassName(String name) throws ClassNotFoundException {
		try (InputStream is = openStreamForClass(name)) {
			return readInputStream(is);
		} catch (Exception e) {
			throw new ClassNotFoundException("class not found exception. name=" + name);
		}
	}

	public byte[] readInputStream(InputStream is) throws IOException {
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream(2048)) {
			for (int i = is.read(); i != -1; i = is.read()) {
				baos.write(i);
			}
			return baos.toByteArray();
		}
	}

	protected InputStream openStreamForClass(String name) {
		String internalName = name.replace('.', '/') + ".class";
		return this.getResourceAsStream(internalName);
	}
}