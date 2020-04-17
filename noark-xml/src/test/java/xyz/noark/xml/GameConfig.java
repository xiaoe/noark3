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
package xyz.noark.xml;

import xyz.noark.core.annotation.tpl.TplAttr;
import xyz.noark.core.annotation.tpl.TplFile;

/**
 * 游戏服务器启动配置对象.
 *
 * @author 小流氓[176543888@qq.com]
 */
@TplFile(value = "game-config.xml")
public class GameConfig {

    @TplAttr(name = "pid")
    private String pid = "dev";

    @TplAttr(name = "sid")
    private int sid = 1;

    @TplAttr(name = "sname")
    private String sname = "研发一区";

    @TplAttr(name = "puc", required = false)
    private int pcu = 3000;

    @TplAttr(name = "mru", required = false)
    private int mru = 18_0000;

    @TplAttr(name = "network.port")
    private int port = 12580;
    @TplAttr(name = "network.heartBeat")
    private int heartBeat = 300;
    @TplAttr(name = "network.crypto")
    private boolean crypto = false;
    @TplAttr(name = "network.compress")
    private boolean compress = false;
    @TplAttr(name = "network.compressThreshold")
    private int compressThreshold = 1024;

    @TplAttr(name = "network.workThreads", required = false)
    private int workThreads = 0;

    @TplAttr(name = "data.templatePath")
    private String templatePath = "/home/wdj/template/";
    @TplAttr(name = "data.saveInterval")
    private int saveInterval = 500;
    @TplAttr(name = "data.offlineInterval")
    private int offlineInterval = 3600;

    @TplAttr(name = "GlobalRedis.ip")
    private String redisIp;

    public String getPid() {
        return pid;
    }

    public int getSid() {
        return sid;
    }

    public String getSname() {
        return sname;
    }

    public int getPcu() {
        return pcu;
    }

    public int getMru() {
        return mru;
    }

    public int getPort() {
        return port;
    }

    public int getHeartBeat() {
        return heartBeat;
    }

    public boolean isCrypto() {
        return crypto;
    }

    public boolean isCompress() {
        return compress;
    }

    public int getCompressThreshold() {
        return compressThreshold;
    }

    public int getWorkThreads() {
        return workThreads;
    }

    public String getTemplatePath() {
        return templatePath;
    }

    public int getSaveInterval() {
        return saveInterval;
    }

    public int getOfflineInterval() {
        return offlineInterval;
    }

    public String getRedisIp() {
        return redisIp;
    }

    @Override
    public String toString() {
        return "GameConfig [pid=" + pid + ", sid=" + sid + ", sname=" + sname + ", pcu=" + pcu + ", mru=" + mru + ", port=" + port + ", heartBeat=" + heartBeat + ", crypto=" + crypto + ", compress=" + compress + ", compressThreshold=" + compressThreshold
                + ", workThreads=" + workThreads + ", templatePath=" + templatePath + ", saveInterval=" + saveInterval + ", offlineInterval=" + offlineInterval + "]";
    }
}