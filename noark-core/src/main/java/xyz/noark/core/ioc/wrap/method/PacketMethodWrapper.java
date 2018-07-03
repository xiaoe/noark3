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
package xyz.noark.core.ioc.wrap.method;

import java.io.Serializable;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import xyz.noark.core.annotation.Controller;
import xyz.noark.core.annotation.PlayerId;
import xyz.noark.core.ioc.definition.method.PacketMethodDefinition;
import xyz.noark.core.ioc.wrap.ParamWrapper;
import xyz.noark.core.ioc.wrap.field.PacketCodecWrapper;
import xyz.noark.core.ioc.wrap.field.PlayerIdParamWrapper;
import xyz.noark.core.ioc.wrap.field.SessionParamWrapper;
import xyz.noark.core.network.Session;
import xyz.noark.reflectasm.MethodAccess;

/**
 * 封包处理方法包装类.
 *
 * @since 3.0
 * @author 小流氓(176543888@qq.com)
 */
public class PacketMethodWrapper extends AbstractControllerMethodWrapper {
	private final Integer opcode;
	private final boolean inner;
	private final ArrayList<ParamWrapper> parameters;

	/** 当前方法是否已废弃使用. */
	private boolean deprecated = false;

	public PacketMethodWrapper(MethodAccess methodAccess, Object single, PacketMethodDefinition md, Controller controller) {
		super(methodAccess, single, md.getMethodIndex(), controller);
		this.opcode = md.getOpcode();
		this.inner = md.isInnerPacket();
		this.printLog = md.isPrintLog();
		this.parameters = new ArrayList<>(md.getParameters().length);

		Arrays.stream(md.getParameters()).forEach(v -> buildParamWrapper(v));
	}

	/** 构建参数 */
	private void buildParamWrapper(Parameter parameter) {
		// Session
		if (Session.class.isAssignableFrom(parameter.getType())) {
			this.parameters.add(new SessionParamWrapper());
		}
		// 玩家ID
		else if (parameter.isAnnotationPresent(PlayerId.class)) {
			this.parameters.add(new PlayerIdParamWrapper());
		}
		// 无法识别的只能依靠Session内置解码器来转化了.
		else {
			this.parameters.add(new PacketCodecWrapper(parameter.getType()));
		}
	}

	/**
	 * 分析参数.
	 * 
	 * @param session Session对象
	 * @param msg 协议封包
	 * @return 参数列表
	 */
	public Object[] analysisParam(Session session, byte[] msg) {
		List<Object> args = new ArrayList<>(parameters.size());
		for (ParamWrapper parameter : parameters) {
			args.add(parameter.read(session, msg));
		}
		return args.toArray();
	}

	/**
	 * 分析参数.
	 * 
	 * @param playerId 玩家ID
	 * @param protocal 协议对象
	 * @return 参数列表
	 */
	public Object[] analysisParam(Serializable playerId, Object protocal) {
		List<Object> args = new ArrayList<>(parameters.size());
		for (ParamWrapper parameter : parameters) {
			args.add(parameter.read(playerId, protocal));
		}
		return args.toArray();
	}

	/**
	 * 判定当前封包处理方法是否被废弃使用.
	 * 
	 * @return 如果被废弃返回true，否则返回false
	 */
	public boolean isDeprecated() {
		return deprecated;
	}

	/**
	 * 设置当前封包处理方法是否被废弃使用.
	 * 
	 * @param deprecated 是否被废弃
	 */
	public void setDeprecated(boolean deprecated) {
		this.deprecated = deprecated;
	}

	/**
	 * 获取封包编号.
	 * 
	 * @return 封包编号
	 */
	public Integer getOpcode() {
		return opcode;
	}

	/**
	 * 是否为内部指令.
	 * <p>
	 * 如果是内部指令，客户端过来的封包是不可以调用此方法的.
	 * 
	 * @return 是否为内部指令
	 */
	public boolean isInner() {
		return inner;
	}

	public void incrCount() {
		// TODO Auto-generated method stub

	}

	@Override
	public String logCode() {
		return "protocal(opcode=" + opcode + ")";
	}
}