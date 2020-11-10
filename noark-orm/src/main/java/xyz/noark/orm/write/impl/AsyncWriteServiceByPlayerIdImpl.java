package xyz.noark.orm.write.impl;

import xyz.noark.orm.EntityMapping;
import xyz.noark.orm.write.AbstractAsyncWriteService;

import java.io.Serializable;

/**
 * 以玩家ID分组存档的回写中心实现.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.4
 */
public class AsyncWriteServiceByPlayerIdImpl extends AbstractAsyncWriteService {

    @Override
    protected <T> Serializable analysisGroupIdByEntity(EntityMapping<T> em, T entity) {
        // 拥有@PlayerId的必属性角色的数据
        if (em.getPlayerId() != null) {
            return em.getPlayerIdValue(entity);
        }
        return DefaultId.INSTANCE;
    }
}
