package xyz.noark.core.ioc.definition.method;

import xyz.noark.core.annotation.configuration.Bean;
import xyz.noark.reflectasm.MethodAccess;

import java.lang.reflect.Method;

/**
 * Bean申明的方法定义.
 *
 * @author 小流氓[176543888@qq.com]
 */
public class BeanMethodDefinition extends SimpleMethodDefinition {
    /**
     * 如果这个方法是申明一个JavaBean的情况指定的Bean名称
     */
    protected final String beanName;

    public BeanMethodDefinition(MethodAccess methodAccess, Method method, Bean bean) {
        super(methodAccess, method);
        this.beanName = bean.name();
    }

    public String getBeanName() {
        return beanName;
    }
}
