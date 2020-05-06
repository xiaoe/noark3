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
package xyz.noark.game.template;

import xyz.noark.core.annotation.Autowired;
import xyz.noark.core.annotation.Service;

import java.util.List;

import static xyz.noark.log.LogHelper.logger;

/**
 * 重载管理器.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.0
 */
@Service
public class ReloadManager {

    @Autowired
    private List<AbstractTemplateManager> abstractTemplateManagers;

    /**
     * 重载所有模板管理器里的数据.
     *
     * @param checkValidity 是否检测数据合法性.
     */
    public void reload(boolean checkValidity) {
        logger.info("loading template data. checkValidity={}", checkValidity);
        abstractTemplateManagers.forEach(v -> {
            logger.info("[{}] loading template.", v.getModuleName());
            v.loadData();
            logger.info("[{}] load OK.", v.getModuleName());
        });
        logger.info("load template data success.");

        if (checkValidity) {
            logger.info("check template data...");
            abstractTemplateManagers.forEach(v -> v.checkValidity());
            logger.info("check template success.");
        }
    }
}