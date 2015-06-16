package cn.eastseven.diancan.service.dao;

import java.util.Collection;

/**
 * Created by dongqi on 15/6/14.
 */
public interface BaseDao {

    public long getNextID(Class<?> clz);

    public long create(Object obj);

    public Object retrieve(Object id, Class<?> clz);

    public Object update(Object obj);

    public long delete(Object id, Class<?> clz);

    public Collection<?> all(Class<?> clz);

    public long deleteAll(Class<?> clz);
}
