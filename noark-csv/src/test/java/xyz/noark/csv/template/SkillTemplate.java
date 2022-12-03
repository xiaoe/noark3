/*
 * Copyright © 2018 www.noark.xyz All Rights Reserved.
 *
 * 感谢您选择Noark框架，希望我们的努力能为您提供一个简单、易用、稳定的服务器端框架 ！
 * 除非符合Noark许可协议，否则不得使用该文件，您可以下载许可协议文件：
 *
 *        http://www.noark.xyz/LICENSE
 *
 * 1.未经许可，任何公司及个人不得以任何方式或理由对本框架进行修改、使用和传播;
 * 2.禁止在本项目或任何子项目的基础上发展任何派生版本、修改版本或第三方版本;
 * 3.无论你对源代码做出任何修改和改进，版权都归Noark研发团队所有，我们保留所有权利;
 * 4.凡侵犯Noark版权等知识产权的，必依法追究其法律责任，特此郑重法律声明！
 */
package xyz.noark.csv.template;

import xyz.noark.core.annotation.tpl.TplAttr;
import xyz.noark.core.annotation.tpl.TplFile;
import xyz.noark.core.lang.IntList;

/**
 * 技能模板表 skill技能
 *
 * @author XDHOLY
 */
@TplFile("Skill.tpl")
public class SkillTemplate {
    /**
     * 编号
     */
    @TplAttr(name = "ID")
    private int id;
    /**
     * 技能名
     */
    @TplAttr(name = "Name")
    private String name;
    /**
     * 技能描述
     */
    @TplAttr(name = "Description")
    private String desc;
    /**
     * 技能图标
     */
    @TplAttr(name = "Icon")
    private String icon;
    /**
     * 技能类型
     */
    @TplAttr(name = "SkillType")
    private int skillType;
    /**
     * 消耗类型
     */
    @TplAttr(name = "SkillCostType")
    private int skillCostType;
    /**
     * 消耗
     */
    @TplAttr(name = "SkillCost")
    private int skillCost;
    /**
     * 施放类型
     */
    @TplAttr(name = "CastType")
    private int castType;
    /**
     * 最大充能层数
     */
    @TplAttr(name = "MaxStock")
    private int maxStock;
    /**
     * 初始充能层数
     */
    @TplAttr(name = "InitStock")
    private int initStock;
    /**
     * 充能时长
     */
    @TplAttr(name = "StockCD")
    private int stockCd;
    /**
     * 技能目标选择
     */
    @TplAttr(name = "TargetType")
    private int targetType;
    /**
     * 指示器
     */
    @TplAttr(name = "TargetFinder")
    private int targetFinder;
    /**
     * 指示器类型
     */
    @TplAttr(name = "TargetFinderType")
    private int targetFinderType;
    /**
     * 指示器参数0
     */
    @TplAttr(name = "TFAtr0")
    private int tfAtr0;
    /**
     * 指示器参数1
     */
    @TplAttr(name = "TFAtr1")
    private int tfAtr1;
    /**
     * 指示器参数2
     */
    @TplAttr(name = "TFAtr2")
    private int tfAtr2;
    /**
     * 技能过滤器
     */
    @TplAttr(name = "TFFilter")
    private int tfFilter;
    /**
     * 技能最小距离
     */
    @TplAttr(name = "MinDistance")
    private int minDistance;
    /**
     * 技能最大距离
     */
    @TplAttr(name = "MaxDistance")
    private int maxDistance;
    /**
     * 是否子弹技能
     */
    @TplAttr(name = "BulletSkill")
    private int bulletSkill;
    /**
     * 是否位移技能
     */
    @TplAttr(name = "RepositionSkill")
    private int repositionSkill;
    /**
     * 冷却时间0
     */
    @TplAttr(name = "CooldownTime0")
    private int cdTime0;
    /**
     * 冷却时间1
     */
    @TplAttr(name = "CooldownTime1")
    private int cdTime1;
    /**
     * CD组
     */
    @TplAttr(name = "CooldownGroup")
    private int cdGroup;

    /**
     * 主动效果列表
     */
    @TplAttr(name = "BuffID0")
    @TplAttr(name = "BuffID1")
    @TplAttr(name = "BuffID2")
    private IntList buffs;

    /**
     * 被动效果列表
     */
    @TplAttr(name = "PassiveBuff0")
    @TplAttr(name = "PassiveBuff1")
    private IntList passiveBuffs;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    public String getIcon() {
        return icon;
    }

    public int getSkillCostType() {
        return skillCostType;
    }

    public int getSkillCost() {
        return skillCost;
    }

    public int getSkillType() {
        return skillType;
    }

    public int getCastType() {
        return castType;
    }

    public int getMaxStock() {
        return maxStock;
    }

    public int getInitStock() {
        return initStock;
    }

    public int getStockCd() {
        return stockCd;
    }

    public int getTargetType() {
        return targetType;
    }

    public int getTargetFinder() {
        return targetFinder;
    }

    public int getTargetFinderType() {
        return targetFinderType;
    }

    public int getTfAtr0() {
        return tfAtr0;
    }

    public int getTfAtr1() {
        return tfAtr1;
    }

    public int getTfAtr2() {
        return tfAtr2;
    }

    public int getTfFilter() {
        return tfFilter;
    }

    public int getMinDistance() {
        return minDistance;
    }

    public int getMaxDistance() {
        return maxDistance;
    }

    public int getBulletSkill() {
        return bulletSkill;
    }

    public int getRepositionSkill() {
        return repositionSkill;
    }

    public int getCdTime0() {
        return cdTime0;
    }

    public int getCdTime1() {
        return cdTime1;
    }

    public int getCdGroup() {
        return cdGroup;
    }

    public IntList getBuffs() {
        return buffs;
    }

    public IntList getPassiveBuffs() {
        return passiveBuffs;
    }
}
