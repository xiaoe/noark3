package xyz.noark.core.lang;

import xyz.noark.core.util.FileUtils;
import xyz.noark.core.util.MathUtils;

/**
 * 一个可以表示文件大小的类，方便配置输入
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.4.3
 */
public class FileSize {
    private final long value;

    public FileSize(long value) {
        this.value = value;
    }

    public FileSize(double value) {
        this(MathUtils.floorLong(value));
    }

    /**
     * 这个文件大小以long类型返回
     *
     * @return long类型的大小数值
     */
    public long longValue() {
        return value;
    }

    /**
     * 这个文件大小以int类型返回,需要注册这里的上限（最大2G）
     *
     * @return int类型的大小数值
     */
    public int intValue() {
        return (int) value;
    }

    @Override
    public String toString() {
        return FileUtils.readableFileSize(value);
    }
}
