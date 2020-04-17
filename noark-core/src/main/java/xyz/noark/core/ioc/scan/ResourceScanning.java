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
package xyz.noark.core.ioc.scan;

import static xyz.noark.log.LogHelper.logger;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import xyz.noark.core.exception.ServerBootstrapException;

/**
 * Class类扫描器.
 *
 * @since 3.0
 * @author 小流氓[176543888@qq.com]
 */
public class ResourceScanning {
	/** URL protocol for a file in the file system: "file" */
	private static final String URL_PROTOCOL_FILE = "file";
	/** URL protocol for an entry from a jar file: "jar" */
	private static final String URL_PROTOCOL_JAR = "jar";
	private static final String BACKSLASH = "/";

	/**
	 * 私有化构造函数,这个类只用一次.
	 */
	private ResourceScanning() {}

	/**
	 * 扫描指定目标下所有资源文件.
	 * <p>
	 * 仅仅只是扫描文件，至于咋处理由回调方法去分析
	 * 
	 * @param packages 扫描的包名
	 * @param callback 扫描到资源后的回调接口
	 */
	public static void scanPackage(String[] packages, ResourceCallback callback) {
		for (String packagePath : packages) {
			scanPackage(packagePath, callback);
		}
	}

	/**
	 * 扫描指定目标下所有资源文件.
	 * 
	 * @param packages 扫描的包名
	 * @param callback 扫描到资源后的回调接口
	 */
	private static void scanPackage(String packagePath, ResourceCallback callback) {
		// 处理一下包名到目录
		packagePath = packagePath.replace('.', '/').replace('\\', '/');
		if (!packagePath.endsWith(BACKSLASH)) {
			packagePath += BACKSLASH;
		}

		try {
			Enumeration<URL> urls = Thread.currentThread().getContextClassLoader().getResources(packagePath);
			while (urls.hasMoreElements()) {
				URL url = urls.nextElement();
				switch (url.getProtocol()) {
				// "file"
				case URL_PROTOCOL_FILE:
					doFindFileResources(packagePath, new File(url.getFile()), callback);
					break;
				// "jar"
				case URL_PROTOCOL_JAR:
					doFindJarResources(url, callback, packagePath);
					break;
				default:
					break;
				}
			}
		} catch (IOException e) {
			throw new ServerBootstrapException("扫描过程中出异常啦", e);
		}
	}

	/**
	 * 扫描指定目录下所有资源文件.
	 * <p>
	 * 仅仅只是扫描文件，至于咋处理由回调方法去分析
	 * 
	 * @param dirs 扫描的目录
	 * @param callback 扫描到资源后的回调接口
	 */
	public static void scanFile(String[] dirs, ResourceCallback callback) {
		for (String dir : dirs) {
			File file = new File(dir);
			doFindFileResources(dir, file, callback);
		}
	}

	private static void doFindJarResources(URL url, ResourceCallback callback, String rootEntryPath) throws IOException {
		JarURLConnection jarCon = (JarURLConnection) url.openConnection();

		try (JarFile jarFile = jarCon.getJarFile()) {
			for (Enumeration<JarEntry> entries = jarFile.entries(); entries.hasMoreElements();) {
				String entryPath = entries.nextElement().getName();
				if (entryPath.startsWith(rootEntryPath)) {
					findJarFile(entryPath, callback);
				}
			}
		}
	}

	/**
	 * 递归扫描目录文件.
	 * 
	 * @param packagePath 包路径
	 * @param file 文件
	 * @param callback 发现文件的回调接口
	 */
	private static void doFindFileResources(String packagePath, File file, ResourceCallback callback) {
		String path = file.getAbsolutePath();

		// 这个目录不存在，忽略
		if (!file.exists()) {
			logger.debug("Skipping [{}] because it does not exist", path);
			return;
		}

		// 这个目录不可以读，忽略
		if (!file.canRead()) {
			logger.warn("Cannot search for matching files underneath directory [{}] because the application is not allowed to read the directory", path);
			return;
		}

		// 如果这是一个目录，继续向下找
		else if (file.isDirectory()) {
			findDir(packagePath, file, callback);
		}

		// 如果是一个文件，交作业
		else if (file.isFile()) {
			findFile(packagePath, file, callback);
		}
	}

	/**
	 * 查找到一个目录.
	 * 
	 * @param packagePath 包路径
	 * @param dir 目录
	 * @param callback 发现文件的回调接口
	 */
	private static void findDir(String packagePath, File dir, ResourceCallback callback) {

		File[] dirContents = dir.listFiles();

		// 目录下没有任何东东，忽略
		if (dirContents == null) {
			logger.warn("Could not retrieve contents of directory [{}]", dir.getAbsolutePath());
			return;
		}

		for (File content : dirContents) {

			// 文件
			if (content.isFile()) {
				findFile(packagePath, content, callback);
			}

			// 是目录则继续
			else if (content.isDirectory()) {
				// 处理目录下的文件，需要把目录修正
				findDir(packagePath + content.getName() + "/", content, callback);
			}
		}
	}

	/**
	 * 查找到一个Jar文件.
	 * 
	 * @param entryPath Jar的资源路径
	 * @param callback 发现文件的回调接口
	 */
	private static void findJarFile(String entryPath, ResourceCallback callback) {
		callback.handle(new JarResource(entryPath));
	}

	/**
	 * 查找到一个文件.
	 * 
	 * @param packagePath 文件所在目录
	 * @param file 文件
	 * @param callback 发现文件的回调接口
	 */
	private static void findFile(String packagePath, File file, ResourceCallback callback) {
		callback.handle(new FileResource(packagePath, file));
	}
}