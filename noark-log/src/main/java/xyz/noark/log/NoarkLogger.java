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
package xyz.noark.log;

/**
 * Noark自己实现的一个Logger
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.0
 */
class NoarkLogger extends AbstractLogger implements Logger {

    NoarkLogger(String name) {
        super(name);
    }

    @Override
    public boolean isDebugEnabled() {
        return isEnabled(Level.DEBUG);
    }

    @Override
    public void debug(String msg) {
        logIfEnabled(Level.DEBUG, msg);
    }

    @Override
    public void debug(String msg, Object... args) {
        logIfEnabled(Level.DEBUG, msg, args);
    }

    @Override
    public boolean isInfoEnabled() {
        return isEnabled(Level.INFO);
    }

    @Override
    public void info(String msg) {
        logIfEnabled(Level.INFO, msg);
    }

    @Override
    public void info(String msg, Object... args) {
        logIfEnabled(Level.INFO, msg, args);
    }

    @Override
    public boolean isWarnEnabled() {
        return isEnabled(Level.WARN);
    }

    @Override
    public void warn(String msg) {
        logIfEnabled(Level.WARN, msg);
    }

    @Override
    public void warn(String msg, Object... args) {
        logIfEnabled(Level.WARN, msg, args);
    }

    @Override
    public boolean isErrorEnabled() {
        return isEnabled(Level.ERROR);
    }

    @Override
    public void error(String msg) {
        logIfEnabled(Level.ERROR, msg);
    }

    @Override
    public void error(String msg, Object... args) {
        logIfEnabled(Level.ERROR, msg, args);
    }
}