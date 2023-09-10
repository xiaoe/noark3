package com.company.game;

import xyz.noark.core.annotation.Controller;
import xyz.noark.core.annotation.controller.RequestMapping;
import xyz.noark.core.annotation.controller.RequestMethod;
import xyz.noark.core.annotation.controller.RequestParam;
import xyz.noark.core.annotation.orm.Json;

import java.util.List;

import static xyz.noark.log.LogHelper.logger;

/**
 * @author 小流氓[176543888@qq.com]
 * @since 3.4
 */
@Controller
public class HttpController {

    @RequestMapping(path = "/test", method = RequestMethod.GET)
    public synchronized String haha(@RequestParam(name = "a") @Json List<String> a) {
        logger.debug("test={}, x={}", a);
        return "test";
    }

}
