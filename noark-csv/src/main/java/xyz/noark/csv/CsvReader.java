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
package xyz.noark.csv;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * CSV解析器.
 *
 * @since 3.0
 * @author 小流氓(176543888@qq.com)
 */
class CsvReader implements AutoCloseable {
	/** 换行符 */
	private static final int LF = '\n';
	private static final int CR = '\r';
	/** 双引号 */
	private static final int QUOTE = '"';

	private final BufferedReader reader;
	private final int separator;
	private final StringBuilder sbing;
	private final Map<String, Integer> headers;
	/** 当前正在处理的字节 */
	private int cur;
	/** 标识当前字节有没有被处理过 */
	private boolean processed = true;

	public CsvReader(char separator, BufferedReader reader) throws IOException {
		this.reader = reader;
		this.separator = separator;
		this.sbing = new StringBuilder(256);
		this.headers = new HashMap<>();

		// 标题先读掉...
		this.readHeaders();
	}

	@Override
	public void close() throws IOException {
		reader.close();
	}

	/**
	 * 获取标题.
	 * 
	 * @return 标题
	 */
	public Map<String, Integer> getHeaders() {
		return headers;
	}

	/**
	 * 获取数据.
	 * 
	 * @return 数据
	 */
	public Stream<String[]> getDatas() throws IOException {
		Stream.Builder<String[]> result = Stream.builder();
		while (!isFileEnd()) {
			String[] line = this.readLine();
			if (line.length > 0) {
				result.add(line);
			}
		}
		return result.build();
	}

	private void readHeaders() throws IOException {
		String[] values = this.readLine();
		for (int i = 0; i < values.length; i++) {
			headers.put(values[i], i);
		}
	}

	private String[] readLine() throws IOException {
		List<String> result = new ArrayList<>();
		for (;;) {
			this.readInt();

			// 换行符,GG
			if (cur == LF || isFileEnd()) {
				this.processed = true;
				break;
			}

			result.add(this.readString());
		}
		return result.toArray(new String[0]);
	}

	/**
	 * ①每条记录占一行；<br>
	 * ②以逗号为分隔符；<br>
	 * ③逗号前后的空格会被忽略；<br>
	 * ④字段中包含有逗号，该字段必须用双引号括起来；<br>
	 * ⑤字段中包含有换行符，该字段必须用双引号括起来；<br>
	 * ⑥字段前后包含有空格，该字段必须用双引号括起来；<br>
	 * ⑦字段中的双引号用两个双引号表示；<br>
	 * ⑧字段中如果有双引号，该字段必须用双引号括起来；<br>
	 * ⑨第一条记录，可以是字段名；<br>
	 * ⑩以上提到的逗号和双引号均为半角字符。<br>
	 */
	private String readString() throws IOException {
		sbing.setLength(0);
		// 是否为双引号的内容
		boolean flag = false;
		for (;;) {
			this.readInt();

			// 判定双引号
			if (!flag && cur == QUOTE) {
				this.processed = true;
				flag = true;
			}

			// 首字节为双引号
			if (flag) {
				this.readInt();
				if (cur == QUOTE) {
					this.processed = true;

					// 双引号结束后，还要找到分隔符
					this.readInt();
					// 分隔符
					if (cur == separator) {
						this.processed = true;
						break;
					}
					// 只要他是换行符要GG
					if (cur == LF) {
						break;
					}
				}
			}
			// 常规字符
			else {
				// 分隔符
				if (cur == separator) {
					this.processed = true;
					break;
				}

				// 只要他是换行符要GG
				if (cur == LF) {
					break;
				}
			}

			this.processed = true;
			sbing.append((char) cur);
		}

		// 删除最后一个\r
		if (sbing.length() > 0 && sbing.charAt(sbing.length() - 1) == CR) {
			sbing.deleteCharAt(sbing.length() - 1);
		}

		return sbing.toString();
	}

	private void readInt() throws IOException {
		if (processed) {
			this.processed = false;
			this.cur = reader.read();
		}
	}

	private boolean isFileEnd() {
		return this.cur == -1;
	}
}