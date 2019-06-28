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
package xyz.noark.game.dfa;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

/**
 * DFA扫描器测试.
 *
 * @since 3.2
 * @author 小流氓(176543888@qq.com)
 */
public class DfaScannerTest {
	private DfaScanner scanner;

	@Before
	public void setUp() {
		this.scanner = new DfaScanner(Arrays.asList("淘宝", "taobao", "出售", "出售元宝", "日", "64"));
	}

	@Test
	public void testContains() {
		assertTrue(scanner.contains("淘64宝64交64易"));
		assertTrue(scanner.contains("淘宝交易"));
		assertTrue(scanner.contains("taobao交易"));
		assertTrue(scanner.contains("TaoBao交易"));
		assertTrue(scanner.contains("ｔａｏｂａｏ交易"));
		assertTrue(scanner.contains("TａｏBａｏ交易"));
		assertTrue(scanner.contains("TTTTａａｏｏBBａａｏｏｏｏ交易"));
		assertTrue(scanner.contains("TａｏBａｏ交易"));
	}

	@Test
	public void testReplace() {
		assertTrue(scanner.replace("64淘44交64易94宝66").equals("**淘44交**易94宝66"));
		assertTrue(scanner.replace("日淘交易宝").equals("*淘交易宝"));
		assertTrue(scanner.replace("淘交易宝").equals("淘交易宝"));
		assertTrue(scanner.replace("淘宝交易").equals("**交易"));
		assertTrue(scanner.replace("taobao交易").equals("******交易"));
		assertTrue(scanner.replace("TaoBao交易ｔａｏｂａｏ").equals("******交易******"));
		assertTrue(scanner.replace("淘交易宝日").equals("淘交易宝*"));
	}

	@Test
	public void testFind() {
		assertTrue(scanner.find("淘交64易宝").isPresent());
		assertTrue(!scanner.find("淘交易宝").isPresent());
		scanner.find("淘宝交易").ifPresent(v -> assertTrue("淘宝".equals(v)));
		scanner.find("taobao交易").ifPresent(v -> assertTrue("taobao".equals(v)));
		scanner.find("交易TaoBao交易ｔａｏｂａｏ").ifPresent(v -> assertTrue("TaoBao".equals(v)));
		scanner.find("交易TaoooBao交易ｔａｏｂａｏ").ifPresent(v -> assertTrue("TaoooBao".equals(v)));
	}

	@Test
	public void testFindAll() {
		assertTrue(!scanner.findAll("64淘交易宝").isEmpty());
		assertTrue(!scanner.findAll("日淘交易宝").isEmpty());
		assertTrue(scanner.findAll("出vs售taobao交易").size() == 2);
		assertTrue(scanner.findAll("交a易TaoBao交易ｔａｏｂａｏ").size() == 2);
		assertTrue(scanner.findAll("交易TaoooBao交易ｔａｏｂａｏ").size() == 2);
		assertTrue(scanner.findAll("taobao交易,出售元宝").size() == 2);
	}

	@Test
	public void testIssuesIr7r1() {
		assertTrue(!scanner.findAll("今天真是个好日子").isEmpty());
		assertTrue(!scanner.findAll("今日天真是个好子").isEmpty());
		assertTrue(!scanner.findAll("日今天日真是个好子").isEmpty());
		assertTrue(!scanner.findAll("今天真日是个好子日").isEmpty());
	}
}