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
package xyz.noark.orm.accessor.sql;

import xyz.noark.core.exception.UnrealizedException;
import xyz.noark.orm.DataConstant;
import xyz.noark.orm.FieldMapping;

/**
 * SQL专家.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.0
 */
public abstract class AbstractSqlExpert implements SqlExpert {

    /**
     * 将来有其他SQL时，如果不合适，就把此层改进为通用型，具体实现放入底层重写.
     * <p>
     * MySql参考： http://dev.mysql.com/doc/refman/5.0/es/connector-j-reference-type- conversions.html
     *
     * @param fm 属性描述对象.
     * @return 返回当前属性对应SQL中的类型.
     */
    protected String evalFieldType(FieldMapping fm) {
        switch (fm.getType()) {
            // Boolean直接写死啦，不可能为其他值的.
            case AsBoolean:
                return "BIT(1)";

            // 字符串类型的，过长需要换类型
            case AsString:
            case AsJson:
                // 如果大于65535，那就要转化为MEDIUMTEXT，大概能存~16M
                if (fm.getWidth() > DataConstant.TEXT_MAX_WIDTH) {
                    return "MEDIUMTEXT";
                }
                // 如果长度等于10K，那就转化为TEXT，大概能存~64K
                else if (fm.getWidth() >= DataConstant.VARCHAT_MAX_WIDTH) {
                    return "TEXT";
                }
                // 其他情况还是使用VarChar方式
                return "VARCHAR(" + fm.getWidth() + ")";

            // 日期类型的，三种，其他用不着就不实现啦.
            case AsInstant:
            case AsLocalDateTime:
            case AsDate:
                return "DATETIME";

            // 数字类型的就写成通用的，Mysql的由子类重写
            case AsInteger:
            case AsAtomicInteger:
            case AsLong:
            case AsLongAdder:
            case AsAtomicLong:
                // 用户自定义了宽度
                if (fm.getWidth() > 0) {
                    return "INT(" + fm.getWidth() + ")";
                }
                // 用数据库的默认宽度
                return "INT";

            case AsDouble:
            case AsFloat:
                // 用户自定义了精度
                if (fm.getWidth() > 0 && fm.getPrecision() > 0) {
                    return "NUMERIC(" + fm.getWidth() + "," + fm.getPrecision() + ")";
                }
                // 用默认精度
                if (fm.isDouble()) {
                    return "NUMERIC(15,10)";
                }
                return "FLOAT";
            case AsBlob:
                // 如果大于65535，那就要转化为MEDIUMTEXT，大概能存~16M
                if (fm.getWidth() > DataConstant.BLOB_MAX_WIDTH) {
                    return "MEDIUMBLOB";
                }
                // 小于65535，就使用Blob
                return "BLOB";
            default:
                throw new UnrealizedException("未实现的Java属性转Mysql类型：" + fm.getType());
        }
    }
}