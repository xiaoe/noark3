package xyz.noark.core.ioc.wrap.exception;


import java.util.Comparator;

/**
 * 异常排序比较器
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.4.7
 */
public class ExceptionDepthComparator implements Comparator<Class<? extends Throwable>> {
    private final Class<? extends Throwable> targetExceptionClass;

    /**
     * 为给定的异常类型创建一个新的ExceptionDepthComparator
     *
     * @param targetExceptionClass 目标异常类型
     */
    public ExceptionDepthComparator(Class<? extends Throwable> targetExceptionClass) {
        this.targetExceptionClass = targetExceptionClass;
    }

    @Override
    public int compare(Class<? extends Throwable> o1, Class<? extends Throwable> o2) {
        int depth1 = getDepth(o1, this.targetExceptionClass, 0);
        int depth2 = getDepth(o2, this.targetExceptionClass, 0);
        return (depth1 - depth2);
    }

    private int getDepth(Class<?> declaredException, Class<?> exceptionToMatch, int depth) {
        if (exceptionToMatch.equals(declaredException)) {
            return depth;
        }
        if (exceptionToMatch == Throwable.class) {
            return Integer.MAX_VALUE;
        }
        return getDepth(declaredException, exceptionToMatch.getSuperclass(), depth + 1);
    }
}
