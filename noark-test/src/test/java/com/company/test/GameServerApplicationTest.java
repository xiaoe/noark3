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
package com.company.test;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.company.test.proto.json.GetBagInfo_CS;
import com.company.test.proto.json.LoginGame_CS;

import xyz.noark.util.ByteArrayUtils;

/**
 * 一个普通Socket的测试.
 *
 * @since 3.0
 * @author 小流氓(176543888@qq.com)
 */
public class GameServerApplicationTest {
	public static void main(String[] args) throws UnknownHostException, IOException, InterruptedException {
		List<Socket> sockets = new ArrayList<>();
		for (int i = 0; i < 100; i++) {
			Socket socket = new Socket("127.0.0.1", 10001);
			socket.getOutputStream().write("json".getBytes());
			socket.getOutputStream().flush();

			Thread.sleep(100);

			LoginGame_CS packet = new LoginGame_CS();
			packet.setUsername("" + i);
			packet.setPassword("" + i);
			String p = JSON.toJSONString(packet);

			socket.getOutputStream().write(ByteArrayUtils.toByteArray((short) (p.length() + 4)));
			socket.getOutputStream().write(ByteArrayUtils.toByteArray(101));
			socket.getOutputStream().write(p.getBytes());
			socket.getOutputStream().flush();

			sockets.add(socket);
		}

		Thread.sleep(5000);
		for (Socket socket : sockets) {
			GetBagInfo_CS packet = new GetBagInfo_CS();
			packet.setType(12);
			String p = JSON.toJSONString(packet);

			socket.getOutputStream().write(ByteArrayUtils.toByteArray((short) (p.length() + 4)));
			socket.getOutputStream().write(ByteArrayUtils.toByteArray(102));
			socket.getOutputStream().write(p.getBytes());
			socket.getOutputStream().flush();
		}

		Thread.sleep(1000000);
	}

}
