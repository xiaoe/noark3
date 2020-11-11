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
package xyz.noark.orm.accessor.sql.mysql.adaptor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import xyz.noark.orm.FieldMapping;
import xyz.noark.orm.accessor.sql.PreparedStatementProxy;

import java.sql.ResultSet;

/**
 * MysqlJson类型属性
 *
 * @author songjie.zheng[39776756@qq.com]
 * @since 3.4
 */
public class MysqlJsonAdaptor extends AbstractValueAdaptor<JSONObject> {
    @Override
    protected void toPreparedStatement(FieldMapping fm, PreparedStatementProxy pstmt, JSONObject jsonObject, int parameterIndex) throws Exception {
        if (jsonObject == null) {
            pstmt.setString(fm, parameterIndex, null);
        } else {
            String json = JSON.toJSONString(jsonObject);
            pstmt.setString(fm, parameterIndex, json);
        }
    }

    @Override
    protected Object toParameter(FieldMapping fm, ResultSet rs) throws Exception {
        String json = rs.getString(fm.getColumnName());
        return JSONObject.parseObject(json, fm.getFieldClass());
    }
}
