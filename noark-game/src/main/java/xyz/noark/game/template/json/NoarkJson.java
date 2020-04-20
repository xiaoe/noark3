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
package xyz.noark.game.template.json;

import com.alibaba.fastjson.JSON;
import xyz.noark.core.annotation.tpl.TplFile;
import xyz.noark.core.exception.TplConfigurationException;
import xyz.noark.core.lang.ResourceLoader;
import xyz.noark.core.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * NoarkJson，内部中转的Json辅助类.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.4
 */
class NoarkJson extends ResourceLoader {

    <T> List<T> loadAll(String templatePath, String zone, Class<T> klass) {
        TplFile file = klass.getAnnotation(TplFile.class);
        if (file == null) {
            throw new TplConfigurationException("这不是JSON格式的配置文件类:" + klass.getName());
        }

        try {
            return JSON.parseArray(readString(templatePath, zone, file.value()), klass);
        } catch (IOException e) {
            throw new TplConfigurationException("JSON格式的配置文件类:" + klass.getName(), e);
        }
    }

    private String readString(String templatePath, String zone, String fileName) throws IOException {
        try (InputStream is = this.newInputStream(templatePath, zone, fileName)) {
            return StringUtils.readString(is);
        }
    }

    <T> T load(String templatePath, String zone, Class<T> klass) {
        TplFile file = klass.getAnnotation(TplFile.class);
        if (file == null) {
            throw new TplConfigurationException("这不是JSON格式的配置文件类:" + klass.getName());
        }

        try (InputStream is = this.newInputStream(templatePath, zone, file.value())) {
            return JSON.parseObject(is, klass);
        } catch (IOException e) {
            throw new TplConfigurationException("JSON格式的配置文件类:" + klass.getName(), e);
        }
    }
}