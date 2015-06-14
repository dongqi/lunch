package cn.eastseven.diancan.service.bo.impl;

import cn.eastseven.diancan.service.bo.UserService;
import cn.eastseven.diancan.service.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.util.logging.Logger;

/**
 * Created by dongqi on 15/6/7.
 */
@Service
public class UserServiceImpl implements UserService {
    private static final Logger log = Logger.getLogger(UserServiceImpl.class.getName());

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Resource(name = "redisTemplate")
    private HashOperations<String, String, Object> hashOperations;

    @Override
    public User get(String username) {
        String key = User.class.getSimpleName() + ":" + username;
        boolean hasKey = redisTemplate.hasKey(key);
        if(hasKey) {
            User user = null;
            try {
                user = User.class.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            for (Field f : user.getClass().getDeclaredFields()) {
                f.setAccessible(true);
                try {
                    Object value = hashOperations.get(key, f.getName());
                    f.set(user, value);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                f.setAccessible(false);
            }
            return user;
        } else {
            User user = new User(username);
            return user;
        }
    }

    @Override
    public User save(User user) {
        String key = user.getClass().getSimpleName() + ":" + user.getName();
        for (Field f : user.getClass().getDeclaredFields()) {
            f.setAccessible(true);
            String hfield = f.getName();
            try {
                Object hvalue = f.get(user);
                hashOperations.put(key, hfield, hvalue);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            f.setAccessible(false);
        }
        return user;
    }

    @Override
    public void saveHost(String host, String name) {
        redisTemplate.boundValueOps(host).set(name);
    }

    @Override
    public String getName(String host) {
        String name = "";
        Object value = redisTemplate.boundValueOps(host).get();
        if(value != null) {
            name = value.toString();
        }
        return name;
    }
}
