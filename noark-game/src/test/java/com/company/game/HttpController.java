package com.company.game;

import xyz.noark.core.annotation.Controller;
import xyz.noark.core.annotation.controller.RequestMapping;
import xyz.noark.core.annotation.controller.RequestMethod;
import xyz.noark.core.annotation.controller.RequestParam;

import java.util.HashSet;
import java.util.Set;

import static xyz.noark.log.LogHelper.logger;

/**
 * @author 小流氓[176543888@qq.com]
 * @since 3.4
 */
@Controller
public class HttpController {

    private Set<Integer> id = new HashSet<  >();

    @RequestMapping(path = "/test", method = RequestMethod.GET, queueId = "playerId")
    public synchronized String haha(@RequestParam(name = "a")int a) {
        logger.debug("test={},", a);
       if( !id.add(a)){
           logger.warn("重复ID={}", a);
       }
        return "test";
    }

}
