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
package xyz.noark.game;

/**
 * Noark的一些常用配置类.
 *
 * @since 3.0
 * @author 小流氓(176543888@qq.com)
 */
public class NoarkConstant {

	/**
	 * 激活Noark的Profile的Key.
	 */
	public static final String NOARK_PROFILES_ACTIVE = "--noark.profiles.active=";

	/**
	 * Noark版本的配置，系统自动生成，不可配置.
	 */
	public static final String NOARK_VERSION = "noark.version";

	/**
	 * 加密公钥，用于配置文件时自定义RSA
	 */
	public static final String CRYPTO_RSA_PUBLICKEY = "crypto.rsa.publickey";

	/**
	 * 配置区服ID的Key
	 */
	public static final String SERVER_ID = "server.id";
	/**
	 * 配置区服名称的Key
	 */
	public static final String SERVER_NAME = "server.name";
	/**
	 * 配置区服是否可以调试的Key
	 */
	public static final String SERVER_DEBUG = "server.debug";
	/**
	 * 配置区服所需策划模板文件的路径
	 */
	public static final String TEMPLATE_PATH = "template.path";

	/**
	 * Banner默认图案为Noark的Logo
	 */
	public static final String BANNER_DEFAULT = "noark.banner";
	/**
	 * Banner悟空版本
	 */
	public static final String BANNER_WUKONG = "wukong.banner";
}