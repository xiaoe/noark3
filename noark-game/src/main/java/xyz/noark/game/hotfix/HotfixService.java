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
package xyz.noark.game.hotfix;

import java.lang.instrument.ClassDefinition;
import java.lang.instrument.UnmodifiableClassException;

/**
 * 提供热修复功能的服务类.
 * <p>
 * 需要在启动命令行添加参数:java -javaagent:agent.jar -jar game-server.jar<br>
 * agent.jar 可以直接使用/noark-game/src/test/resources目录下已准备的
 *
 * @since 3.1
 * @author 小流氓(176543888@qq.com)
 */
public class HotfixService {

	/**
	 * 使用类生成的字节数组来修复指定类.
	 * 
	 * @param className 指定类名称
	 * @param theClassFile 类生成的字节数组
	 * @return 修复结果,文本提示
	 */
	public static String fix(String className, byte[] theClassFile) {
		if (JavaAgent.INST == null) {
			return "启动命令行添加参数:java -javaagent:agent.jar -jar game-server.jar";
		}

		try {
			Class<?> theClass = Class.forName(className);
			JavaAgent.INST.redefineClasses(new ClassDefinition(theClass, theClassFile));
		} catch (ClassNotFoundException e) {
			return "未找到目标类,需要类全称.例如:xyz.noark.game.hotfix.HotfixService";
		} catch (UnmodifiableClassException e) {
			return "热更的时候发生未知错误,请检查:" + e.getMessage();
		}
		return "OK";
	}
}