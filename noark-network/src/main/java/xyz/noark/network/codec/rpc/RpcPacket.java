/*
 * Copyright © 2018 huiyunetwork.com All Rights Reserved.
 *
 * 感谢您加入辉娱网络，不用多久，您就会升职加薪、当上总经理、出任CEO、迎娶白富美、从此走上人生巅峰
 * 除非符合本公司的商业许可协议，否则不得使用或传播此源码，您可以下载许可协议文件：
 *
 * 		http://www.huiyunetwork.com/LICENSE
 *
 * 1、未经许可，任何公司及个人不得以任何方式或理由来修改、使用或传播此源码;
 * 2、禁止在本源码或其他相关源码的基础上发展任何派生版本、修改版本或第三方版本;
 * 3、无论你对源代码做出任何修改和优化，版权都归辉娱网络所有，我们将保留所有权利;
 * 4、凡侵犯辉娱网络相关版权或著作权等知识产权者，必依法追究其法律责任，特此郑重法律声明！
 */
package xyz.noark.network.codec.rpc;

import xyz.noark.core.network.NetworkPacket;
import xyz.noark.network.codec.DefaultNetworkPacket;

/**
 * Rpc封包。
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.4.7
 */
public class RpcPacket extends DefaultNetworkPacket implements NetworkPacket {
    /**
     * 请求标识，true=请求，false=响应
     */
    private boolean reqFlag = false;
    /**
     * 请求ID
     */
    private int reqId;

    public boolean isReqFlag() {
        return reqFlag;
    }

    public void setReqFlag(boolean reqFlag) {
        this.reqFlag = reqFlag;
    }

    public int getReqId() {
        return reqId;
    }

    public void setReqId(int reqId) {
        this.reqId = reqId;
    }
}
