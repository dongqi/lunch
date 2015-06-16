package cn.eastseven.diancan.service.bo.impl;

import cn.eastseven.diancan.service.bo.UserService;
import cn.eastseven.diancan.service.dao.BaseDao;
import cn.eastseven.diancan.service.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collection;
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

    @Autowired
    private BaseDao dao;

    @Override
    public User get(String username) {
        User target = null;
        Collection<User> users = (Collection<User>) dao.all(User.class);
        for(User user : users) {
            if(user.getName().equalsIgnoreCase(username)) {
                target = user;
                break;
            }
        }

        if(target == null) {
            return new User("");
        }
        return target;
    }

    @Override
    public User save(User user) {
        long id = dao.create(user);
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
        if (value != null) {
            name = value.toString();
        }
        return name;
    }
}
