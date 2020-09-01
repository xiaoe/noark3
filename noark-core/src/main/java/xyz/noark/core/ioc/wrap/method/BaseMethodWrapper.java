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
package xyz.noark.core.ioc.wrap.method;

import xyz.noark.core.annotation.Order;
import xyz.noark.core.ioc.wrap.MethodWrapper;
import xyz.noark.reflectasm.MethodAccess;

/**
 * 一个可执行的方法.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.0
 */
public class BaseMethodWrapper implements MethodWrapper {
    /**
     * 缓存那个单例对象
     */
    protected final Object single;
    protected final int methodIndex;
    protected final MethodAccess methodAccess;
    private final int order;

    public BaseMethodWrapper(Object single, MethodAccess methodAccess, int methodIndex, Order order) {
        this.single = single;
        this.methodIndex = methodIndex;
        this.methodAccess = methodAccess;
        this.order = order == null ? Integer.MAX_VALUE : order.value();
    }

    @Override
    public Object invoke(Object... args) {
        if (args.length == 0) {
            return methodAccess.invoke(single, methodIndex);
        } else {
            return methodAccess.invoke(single, methodIndex, args);
        }
    }

    @Override
    public int getOrder() {
        return order;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + methodIndex;
        result = prime * result + ((single == null) ? 0 : single.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        BaseMethodWrapper other = (BaseMethodWrapper) obj;
        if (methodIndex != other.methodIndex) {
            return false;
        }
        if (single == null) {
            if (other.single != null) {
                return false;
            }
        } else if (!single.equals(other.single)) {
            return false;
        }
        return true;
    }
}