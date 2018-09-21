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

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import xyz.noark.core.util.StringUtils;

/**
 * 基于DFA算法构建的敏感词扫描器.
 * <p>
 * 实现目标：<br>
 * 1、大小写<br>
 * 2、全角半角<br>
 * 3、停顿词<br>
 * 4、重复词<br>
 * 
 * @since 3.2
 * @author 小流氓(176543888@qq.com)
 */
public class DfaScanner {
	/** 全角对应于ASCII表的可见字符从！开始，偏移值为65281 */
	private static final char SBC_CHAR_START = 65281;
	/** 全角对应于ASCII表的可见字符到～结束，偏移值为65374 */
	private static final char SBC_CHAR_END = 65374;
	/** 全角空格的值，它没有遵从与ASCII的相对偏移，必须单独处理 */
	private static final char SBC_SPACE = 12288;
	/** 半角空格的值，在ASCII中为32(Decimal) */
	private static final char DBC_SPACE = ' ';

	/** ASCII表中除空格外的可见字符与对应的全角字符的相对偏移 */
	private static final int CONVERT_STEP = 65248;
	/** 发现敏感词后替换显示的字符 */
	private static final char SHOW_SIGN = '*';

	/** 分隔符号 */
	private final Set<Integer> separatesSymbols = new HashSet<>();
	/** 敏感词库 */
	private final Map<Integer, DfaNode> nodes = new HashMap<>();

	/**
	 * 构建一个敏感词扫描器.
	 * 
	 * <pre>
	 * 默认的分隔停顿符：`~!1@2#3$4%5^6&amp;7*8(9)0_-+={[}]|\\:;\&quot;'&lt;,&gt;.?/！￥%……｛｝【】
	 * </pre>
	 * 
	 * @param sensitivewords 敏感词库
	 */
	public DfaScanner(List<String> sensitivewords) {
		this(" `~!1@2#3$4%5^6&7*8(9)0_-+={[}]|\\:;\"'<,>.?/！￥%……｛｝【】", sensitivewords);
	}

	/**
	 * 构建一个敏感词扫描器.
	 * 
	 * @param symbols 分隔停顿符
	 * @param sensitivewords 敏感词列表
	 */
	public DfaScanner(String symbols, List<String> sensitivewords) {
		this.initSeparatesSymbol(symbols);
		this.initSensitiveWords(sensitivewords);
	}

	/**
	 * 初始化分隔停顿符
	 * 
	 * @param symbols 分隔停顿符
	 */
	private void initSeparatesSymbol(String symbols) {
		for (int i = 0, len = symbols.length(); i < len; i++) {
			this.separatesSymbols.add(charConvert(symbols.charAt(i)));
		}
	}

	/**
	 * 初始化敏感词库
	 * 
	 * @param sensitivewords 敏感词列表
	 */
	private void initSensitiveWords(List<String> sensitivewords) {
		for (String word : sensitivewords) {
			if (StringUtils.isEmpty(word)) {
				continue;
			}

			char[] array = word.toCharArray();
			final int first = charConvert(array[0]);
			DfaNode fnode = nodes.computeIfAbsent(first, key -> new DfaNode(first, array.length == 1));
			for (int i = 1, len = array.length, lastIndex = array.length - 1; i < len; i++) {
				fnode = fnode.addIfAbsent(charConvert(array[i]), i == lastIndex);
			}
		}
	}

	/**
	 * 全角转换半角
	 */
	private int qj2bj(char src) {
		// 如果位于全角！到全角～区间内
		if (src >= SBC_CHAR_START && src <= SBC_CHAR_END) {
			return src - CONVERT_STEP;
		}
		// 如果是全角空格
		else if (src == SBC_SPACE) {
			return DBC_SPACE;
		}
		return src;
	}

	/**
	 * 大写转化为小写,全角转化为半角
	 */
	private int charConvert(char src) {
		int r = qj2bj(src);
		// 大写转化为小写
		return (r >= 'A' && r <= 'Z') ? r + 32 : r;
	}

	/**
	 * 判定指定文本中是否包含了敏感词.
	 * <p>
	 * 应用场景：玩家创角验证名称，创建工会验证名称等
	 * 
	 * @param text 指定文本
	 * @return 如果存在敏感词则返回true,否则返回false
	 */
	public boolean contains(final String text) {
		return this.analysis(text, 1, false).isExist();
	}

	/**
	 * 替换指定文本中的敏感词为星号(*).
	 * <p>
	 * 应用场景：聊天过滤，公会公告等过滤
	 * 
	 * @param text 指定文本
	 * @return 替换敏感词后的文本，如果没有敏感词返回原来的文本
	 */
	public String replace(final String text) {
		return this.analysis(text, 0, true).getText();
	}

	/**
	 * 查找出指定文本中包含的第一个敏感词并返回.
	 * 
	 * @param text 指定文本
	 * @return 指定文本中包含的第一个敏感词
	 */
	public Optional<String> find(final String text) {
		return this.analysis(text, 1, false).getWords().stream().findFirst();
	}

	/**
	 * 查找出指定文本中包含的全部敏感词并返回.
	 * 
	 * @param text 指定文本
	 * @return 指定文本中包含的全部敏感词集合
	 */
	public List<String> findAll(final String text) {
		return this.analysis(text, 0, false).getWords();
	}

	/**
	 * 查找一个文本中是否包含了敏感字.
	 * 
	 * @param text 文本
	 * @return 敏感字
	 */
	private DfaResult analysis(final String text, int limit, boolean replace) {
		final DfaResult result = new DfaResult();
		boolean flag = false;
		final char[] array = text.toCharArray();
		for (int i = 0, length = array.length; i < length; i++) {
			// 当前正在检查的字符
			int cur = charConvert(array[i]);
			DfaNode node = nodes.get(cur);
			if (node == null) {
				continue;
			}
			boolean mark = false;
			int markIndex = -1;
			// 单字匹配
			if (node.isLast()) {
				mark = true;
			}
			// 当前检查字符的备份
			int backups = cur;
			for (int k = i + 1; k < length; k++) {
				int temp = charConvert(array[k]);
				// 重复过滤
				if (temp == backups) {
					continue;
				}
				// 停顿符
				if (separatesSymbols.contains(temp)) {
					continue;
				}
				// 查找子节点
				node = node.querySub(temp);
				if (node == null) {
					break;
				}

				backups = temp;
				markIndex = k;

				// 匹配最长，不跳出此轮循环
				if (node.isLast()) {
					mark = true;
					// 如果只要一个敏感词，基本就是判定有没有，可以结果
					if (limit == 1) {
						break;
					}
				}
			}

			if (mark) {
				flag = true;
				// 分析出来的敏感词
				result.addSensitiveWord(new String(array, i, markIndex - i + 1));
				// 替换需求
				if (replace) {
					for (int k = i; k <= markIndex; k++) {
						array[k] = SHOW_SIGN;
					}
				}
				i = markIndex;
				// 如果要查找有限制数量，那能少找几个是几个...
				if (limit > 0 && result.getWords().size() >= limit) {
					break;
				}
			}
		}
		// 处理后的文本
		result.setExist(flag);
		result.setText(flag ? new String(array) : text);
		return result;
	}
}