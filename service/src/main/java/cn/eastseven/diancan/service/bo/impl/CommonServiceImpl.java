package cn.eastseven.diancan.service.bo.impl;

import cn.eastseven.diancan.service.bo.CommonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * Created by dongqi on 15/6/7.
 */
@Service
public class CommonServiceImpl implements CommonService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public long getSequenceNextValue(Class<?> clz) {
        long seq = redisTemplate.boundHashOps(SEQ).increment(clz.getSimpleName(), 1);
        return seq;
    }

}
