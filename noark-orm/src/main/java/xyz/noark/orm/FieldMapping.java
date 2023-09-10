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
package xyz.noark.orm;

import xyz.noark.core.annotation.Gzip;
import xyz.noark.core.annotation.PlayerId;
import xyz.noark.core.annotation.orm.*;
import xyz.noark.core.util.FieldUtils;
import xyz.noark.core.util.StringUtils;
import xyz.noark.orm.accessor.FieldType;
import xyz.noark.reflectasm.MethodAccess;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

/**
 * 属性映射描述类.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.0
 */
public class FieldMapping {
    private final Field field;
    private final Type klass;
    private final Id id;
    private final GeneratedValue generatedValue;
    private final Column column;
    private final PlayerId playerId;
    private final Json json;
    private final Collate collate;
    /**
     * 创建时间
     */
    private final CreatedDate createdDate;
    /**
     * 最后修改时间
     */
    private final LastModifiedDate lastModifiedDate;
    private final int getMethodIndex;
    private final int setMethodIndex;
    private FieldType type;
    private String columnName;
    private int width;
    /**
     * 是否有可能为Emoji
     */
    private boolean emoji;

    private Gzip gzip;

    public FieldMapping(Field field, MethodAccess methodAccess) {
        this.field = field;
        this.field.setAccessible(true);
        this.klass = field.getGenericType();

        // 所有注解
        this.id = field.getAnnotation(Id.class);
        this.generatedValue = field.getAnnotation(GeneratedValue.class);
        this.column = field.getAnnotation(Column.class);
        this.collate = field.getAnnotation(Collate.class);
        this.playerId = field.getAnnotation(PlayerId.class);
        this.json = field.getAnnotation(Json.class);
        this.createdDate = field.getAnnotation(CreatedDate.class);
        this.lastModifiedDate = field.getAnnotation(LastModifiedDate.class);
        this.emoji = field.isAnnotationPresent(Emoji.class);
        this.gzip = field.getAnnotation(Gzip.class);

        this.getMethodIndex = methodAccess.getIndex(FieldUtils.genGetMethodName(field));
        this.setMethodIndex = methodAccess.getIndex(FieldUtils.genSetMethodName(field));
    }

    public Field getField() {
        return field;
    }

    public Type getFieldClass() {
        return klass;
    }

    public Column getColumn() {
        return column;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public FieldType getType() {
        return type;
    }

    void setType(FieldType type) {
        this.type = type;
    }

    public boolean hasGeneratedValue() {
        return generatedValue != null;
    }

    public boolean isPrimaryId() {
        return id != null;
    }

    public boolean isPlayerId() {
        return playerId != null;
    }

    public int getPrecision() {
        return column == null ? 15 : column.precision();
    }

    public int getScale() {
        return column == null ? 5 : column.scale();
    }

    public boolean isNotNull() {
        return column != null && !column.nullable();
    }

    public boolean hasDefaultValue() {
        return column != null && !"".equals(column.defaultValue());
    }

    public String getDefaultValue() {
        return column.defaultValue();
    }

    public boolean hasColumnComment() {
        return !StringUtils.isEmpty(this.getColumnComment());
    }

    public String getColumnComment() {
        return column == null ? "" : column.comment();
    }

    public int getGetMethodIndex() {
        return getMethodIndex;
    }

    public int getSetMethodIndex() {
        return setMethodIndex;
    }

    public boolean hasCollate() {
        return collate != null;
    }

    public String getCollateValue() {
        return collate.value();
    }

    // ----------- 类型判定 ---------------------------

    public boolean isString() {
        return String.class == klass;
    }

    public boolean isBoolean() {
        return klass == boolean.class || klass == Boolean.class;
    }

    public boolean isInt() {
        return klass == int.class || klass == Integer.class;
    }

    public boolean isLong() {
        return klass == long.class || klass == Long.class;
    }

    /**
     * @return 当前对象是否为浮点
     */
    public boolean isFloat() {
        return klass == float.class || klass == Float.class;
    }

    /**
     * @return 当前对象是否为双精度浮点
     */
    public boolean isDouble() {
        return klass == double.class || klass == Double.class;
    }

    public boolean isBlob() {
        return field.isAnnotationPresent(Blob.class) || klass == byte[].class;
    }

    public boolean isJson() {
        return json != null;
    }

    public boolean isCreatedDate() {
        return createdDate != null;
    }

    public boolean isLastModifiedDate() {
        return lastModifiedDate != null;
    }

    public boolean isEmoji() {
        return emoji;
    }

    public void setEmoji(boolean emoji) {
        this.emoji = emoji;
    }

    public boolean isGzip() {
        return gzip != null;
    }

    public int getGzipThreshold() {
        return gzip.threshold();
    }
}