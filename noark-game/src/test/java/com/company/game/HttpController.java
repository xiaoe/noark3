package com.company.game;

import xyz.noark.core.annotation.Controller;
import xyz.noark.core.annotation.controller.RequestMapping;
import xyz.noark.core.annotation.controller.RequestMethod;

import static xyz.noark.log.LogHelper.logger;

/**
 * @author 小流氓[176543888@qq.com]
 * @since 3.4
 */
@Controller
public class HttpController {

    @RequestMapping(path = "/test", method = RequestMethod.GET, queueId = "playerId")
    public String haha() {
        logger.debug("test");
        return "test";
    }

}
