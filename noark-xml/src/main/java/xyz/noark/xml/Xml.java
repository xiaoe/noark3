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
package xyz.noark.xml;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

import xyz.noark.core.annotation.tpl.TplFile;
import xyz.noark.core.exception.FileNotFoundException;
import xyz.noark.core.exception.TplConfigurationException;

/**
 * 史上最快的XML序列化工具包.
 * <p>
 * 如此装逼，真的好吗？我感觉挺好的，不装一下逼，谁又能知道你的横空出世呢~
 *
 * @since 3.1
 * @author 小流氓(176543888@qq.com)
 */
public class Xml {

	/**
	 * 根据指定类文件加载XML格式的配置.
	 * <p>
	 * Load文件-->SAX解析-->修补EL表达式-->填充对象.
	 * 
	 * @param klass 配置类.
	 * @return 返回配置类对象就算文件不存在也不会返回空.
	 */
	public static <T> T load(Class<T> klass) {
		TplFile file = klass.getAnnotation(TplFile.class);
		if (file == null) {
			throw new TplConfigurationException("这不是XML格式的配置文件类:" + klass.getName());
		}

		try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(file.value())) {
			ObjectXmlHandler<T> handler = new ObjectXmlHandler<>(klass, file.value());
			SAXParserFactory.newInstance().newSAXParser().parse(is, handler);
			return handler.getResult();
		} catch (IOException e) {
			throw new FileNotFoundException("XML配置文件未找到." + file.value(), e);
		} catch (SAXException | ParserConfigurationException e) {
			throw new TplConfigurationException("格式异常:" + klass.getName());
		}
	}

	/**
	 * 根据指定类文件加载XML格式的配置.
	 * <p>
	 * Load文件-->SAX解析-->修补EL表达式-->填充对象.
	 * 
	 * @param klass 配置类.
	 * @param path 配置文件路径
	 * @return 返回配置类对象就算文件不存在也不会返回空.
	 */
	public static <T> T load(Class<T> klass, Path path) {
		try (InputStream is = Files.newInputStream(path, StandardOpenOption.READ)) {
			ObjectXmlHandler<T> handler = new ObjectXmlHandler<>(klass, path.toString());
			SAXParserFactory.newInstance().newSAXParser().parse(is, handler);
			return handler.getResult();
		} catch (IOException e) {
			throw new FileNotFoundException("XML配置文件未找到." + path, e);
		} catch (SAXException | ParserConfigurationException e) {
			throw new TplConfigurationException("格式异常:" + klass.getName());
		}
	}

	/**
	 * 根据指定类文件加载XML格式的模板.
	 * 
	 * @param templatePath 模板文件路径
	 * @param klass 模板类文件
	 * @return 模板类对象的集合
	 */
	public static <T> List<T> loadAll(String templatePath, Class<T> klass) {
		TplFile file = klass.getAnnotation(TplFile.class);
		if (file == null) {
			throw new TplConfigurationException("这不是XML格式的配置文件类:" + klass.getName());
		}

		try (InputStream is = Files.newInputStream(Paths.get(templatePath, file.value()), StandardOpenOption.READ)) {
			ArrayXmlHandler<T> myhandler = new ArrayXmlHandler<>(klass, file.value());
			SAXParserFactory.newInstance().newSAXParser().parse(is, myhandler);
			return myhandler.getResult();
		} catch (IOException e) {
			throw new FileNotFoundException("XML配置文件未找到." + file.value(), e);
		} catch (SAXException | ParserConfigurationException e) {
			throw new TplConfigurationException("格式异常:" + klass.getName(), e);
		}
	}
}