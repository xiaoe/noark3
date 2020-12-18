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
package xyz.noark.orm.accessor.sql.mysql.adaptor;

import xyz.noark.core.util.GzipUtils;
import xyz.noark.orm.FieldMapping;
import xyz.noark.orm.accessor.sql.PreparedStatementProxy;

import java.sql.ResultSet;
import java.sql.Types;

import static xyz.noark.log.LogHelper.logger;

/**
 * Blob类型属性
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.0
 */
class BlobAdaptor extends AbstractValueAdaptor<Object> {

    @Override
    protected void toPreparedStatement(FieldMapping fm, PreparedStatementProxy pstmt, Object value, int parameterIndex) throws Exception {
        if (value == null) {
            pstmt.setNull(parameterIndex, Types.BLOB);
        } else {
            byte[] array = (byte[]) value;
            int length = array.length;
            // 当且仅当开启了压缩且数据大于压缩阀值
            if (fm.isGzip() && fm.getGzipThreshold() > length) {
                array = GzipUtils.compress(array);
                logger.debug("Gzip compress. {}->{}", length, array.length);
            }
            pstmt.setObject(parameterIndex, array);
        }
    }

    @Override
    protected Object toParameter(FieldMapping fm, ResultSet rs) throws Exception {
        byte[] array = (byte[]) rs.getObject(fm.getColumnName());
        if (array == null) {
            return null;
        }

        // 如果这是属性开启了Gzip压缩且他是个Gzip压缩数据
        if (fm.isGzip() && GzipUtils.isGzip(array)) {
            int length = array.length;
            array = GzipUtils.uncompress(array);
            logger.debug("Gzip uncompress. {}->{}", length, array.length);
        }

        return array;
    }
}