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

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * DFA算法分析结果.
 *
 * @since 3.2
 * @author 小流氓[176543888@qq.com]
 */
class DfaResult {
	/** 处理过的文本 */
	private String text;
	/** 是否存在敏感词 */
	private boolean exist;
	/** 找出来的敏感词 */
	private List<String> words;

	public void addSensitiveWord(String word) {
		if (words == null) {
			words = new LinkedList<>();
		}
		this.words.add(word);
	}

	public void setExist(boolean exist) {
		this.exist = exist;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}

	public boolean isExist() {
		return exist;
	}

	public List<String> getWords() {
		return words == null ? Collections.emptyList() : words;
	}

	@Override
	public String toString() {
		return "DfaResult [text=" + text + ", flag=" + exist + ", result=" + words + "]";
	}
}