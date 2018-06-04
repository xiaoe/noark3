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
package xyz.noark.log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

/**
 * 日志文件写入器.
 *
 * @since 3.0
 * @author 小流氓(176543888@qq.com)
 */
class LogFileWriter {
	private int lastWriterHour = -1;
	private FileWriter fileWriter = null;

	/**
	 * 写入文本文件中.
	 * 
	 * @param date 日志时间.
	 * @param text 文本日志
	 * @throws IOException 可能会抛出IO异常.
	 */
	void writer(LocalDateTime date, String text) throws IOException {
		// 不是同一时间，切换输出目标
		if (fileWriter == null || date.getHour() != lastWriterHour) {

			// 如果上一个输出流存在，先关闭...
			if (fileWriter != null) {
				fileWriter.close();
			}

			String filename = LogConfigurator.LOG_PATH.getPath(date);
			File file = new File(filename.toString());
			if (!file.exists()) {
				File fileParent = file.getParentFile();
				if (!fileParent.exists()) {
					fileParent.mkdirs();
				}
				file.createNewFile();
			}
			fileWriter = new FileWriter(file, true);
		}

		fileWriter.write(text);
		fileWriter.flush();
	}
}