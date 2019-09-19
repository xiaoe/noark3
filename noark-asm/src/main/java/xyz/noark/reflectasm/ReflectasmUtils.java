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
package xyz.noark.reflectasm;

/**
 * Reflectasm工具类
 *
 * @since 3.3.4
 * @author 小流氓(176543888@qq.com)
 */
public class ReflectasmUtils {
	private static final String NOARK_PACKAGE_NAME = "xyz.noark.";

	/**
	 * 重新构建类名称.
	 * <p>
	 * 为了一些特别的功能，需要把动态生成的类移出原来目录，在这里当然会丢失同级包方法访问的权限，但是谁又不会没事需要这个权限呢. <br>
	 * 非xyz.noark开关的包名，就要前面添加一个前缀
	 * 
	 * @param className 类名
	 * @return 返回处理后的新名称
	 */
	public static String rebuildClassName(String className) {
		if (!className.startsWith(NOARK_PACKAGE_NAME)) {
			className = NOARK_PACKAGE_NAME + className;
		}
		return className;
	}
}