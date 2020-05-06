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
package xyz.noark.csv.template;

import xyz.noark.core.annotation.tpl.TplAttr;
import xyz.noark.core.annotation.tpl.TplFile;

/**
 * 道具模板表
 *
 * @author 小流氓
 */
@TplFile("Item.tpl")
public class ItemTemplate {
    /**
     * 道具编号
     */
    @TplAttr(name = "Id")
    private String id;

    /**
     * 道具名称
     */
    @TplAttr(name = "Name")
    private String name;

    /**
     * 标题
     */
    @TplAttr(name = "Title")
    private String title;

    /**
     * 品质
     */
    @TplAttr(name = "Quality")
    private int quality;

    /**
     * 道具图标
     */
    @TplAttr(name = "ICON")
    private String icon;

    /**
     * 能否直接使用
     */
    @TplAttr(name = "CanUse")
    private int canUse;

    /**
     * 功能类型
     */
    @TplAttr(name = "Func")
    private int func;

    /**
     * 功能参数1
     */
    @TplAttr(name = "Parameter1")
    private int parameter1;

    /**
     * 功能参数1
     */
    @TplAttr(name = "Parameter2")
    private int parameter2;

    /**
     * 描述
     */
    @TplAttr(name = "Describe")
    private String describe;

    /**
     * 功能跳转
     */
    @TplAttr(name = "Goto")
    private String goTo;

    /**
     * 使用等级
     */
    @TplAttr(name = "UseLevel")
    private int useLevel;

    /**
     * 使用次数
     */
    @TplAttr(name = "UseTimes")
    private int useTimes;

    /**
     * 这里只是测试，随便抓两个配置来充一下
     */
    @TplAttr(name = "Name")
    @TplAttr(name = "UseLevel")
    private Reward rewards;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getTitle() {
        return title;
    }

    public int getQuality() {
        return quality;
    }

    public String getIcon() {
        return icon;
    }

    public int getCanUse() {
        return canUse;
    }

    public int getFunc() {
        return func;
    }

    public int getParameter1() {
        return parameter1;
    }

    public int getParameter2() {
        return parameter2;
    }

    public String getDescribe() {
        return describe;
    }

    public String getGoTo() {
        return goTo;
    }

    public int getUseLevel() {
        return useLevel;
    }

    public int getUseTimes() {
        return useTimes;
    }

    public Reward getRewards() {
        return rewards;
    }

    @Override
    public String toString() {
        return "ItemTemplate [id=" + id + ", useTimes=" + useTimes + "]";
    }
}