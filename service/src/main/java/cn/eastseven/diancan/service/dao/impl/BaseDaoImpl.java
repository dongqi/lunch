package cn.eastseven.diancan.service.dao.impl;

import cn.eastseven.diancan.service.dao.BaseDao;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Created by dongqi on 15/6/14.
 */
@Component
public class BaseDaoImpl implements BaseDao {

    private Logger log = Logger.getLogger(getClass().getName());

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Resource(name = "redisTemplate")
    private HashOperations<String, String, Object> hashOperations;

    public static final String SEQ = "sequence";

    @Override
    public long getNextID(Class<?> clz) {
        String key = clz.getSimpleName();
        return hashOperations.increment(SEQ, key, 1);
    }

    @Override
    public long create(Object obj) {
        long id = getNextID(obj.getClass());
        String key = obj.getClass().getSimpleName() + ":" + id;
        for (Field field : obj.getClass().getDeclaredFields()) {
            String hField = field.getName();
            try {
                field.setAccessible(true);
                Object hValue = field.get(obj);
                log.info("key=" + key + ", hf=" + hField + ", hv=" + hValue.getClass() + ", " + field.getGenericType());
                if (hField.equalsIgnoreCase("id")) hValue = id;
                hashOperations.put(key, hField, hValue);
                field.setAccessible(false);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return id;
    }

    @Override
    public Object retrieve(Object id, Class<?> clz) {
        String key = clz.getSimpleName() + ":" + id;
        Object obj = null;
        try {
            obj = clz.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        Set<String> keys = hashOperations.keys(key);
        for (String fieldName : keys) {
            try {
                Object value = hashOperations.get(key, fieldName);
                Field field = clz.getDeclaredField(fieldName);
                field.setAccessible(true);
                field.set(obj, value);
                field.setAccessible(false);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return obj;
    }

    @Override
    public Object update(Object obj) {
        try {
            Field idField = obj.getClass().getDeclaredField("id");
            idField.setAccessible(true);
            Object id = idField.get(obj);
            idField.setAccessible(false);
            String key = obj.getClass().getSimpleName() + ":" + id;
            Object target = retrieve(id, obj.getClass());
            for (Field field : target.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                String hField = field.getName();
                Field originField = obj.getClass().getDeclaredField(hField);
                originField.setAccessible(true);
                Object hValue = originField.get(obj);
                originField.setAccessible(false);
                field.set(target, hValue);
                hashOperations.put(key, hField, hValue);
                field.setAccessible(false);
            }

        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return obj;
    }

    @Override
    public long delete(Object id, Class<?> clz) {
        String pattern = clz.getSimpleName() + ":" + id;
        Set<String> keys = redisTemplate.keys(pattern);
        redisTemplate.delete(keys);
        return keys.size();
    }

    @Override
    public Collection<?> all(Class<?> clz) {
        List<Object> result = Lists.newArrayList();
        Set<String> keys = redisTemplate.keys(clz.getSimpleName() + "*");
        List<String> _keys = Lists.newArrayList();
        _keys.addAll(keys);
        Collections.sort(_keys);

        for (String k : _keys) {
            try {
                Object obj = clz.newInstance();
                Set<String> objKeys = hashOperations.keys(k);
                for (String hk : objKeys) {
                    Field f = obj.getClass().getDeclaredField(hk);
                    f.setAccessible(true);
                    f.set(obj, hashOperations.get(k, hk));
                    f.setAccessible(false);
                }
                result.add(obj);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }

        }
        return result;
    }

    @Override
    public long deleteAll(Class<?> clz) {
        String pattern = clz.getSimpleName() + "*";
        Set<String> keys = redisTemplate.keys(pattern);
        redisTemplate.delete(pattern);
        return keys.size();
    }
}
