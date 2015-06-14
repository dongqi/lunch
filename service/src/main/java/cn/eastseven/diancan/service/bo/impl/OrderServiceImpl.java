package cn.eastseven.diancan.service.bo.impl;

import cn.eastseven.diancan.service.bo.CommonService;
import cn.eastseven.diancan.service.bo.OrderService;
import cn.eastseven.diancan.service.model.FoodItem;
import cn.eastseven.diancan.service.model.Order;
import cn.eastseven.diancan.service.model.User;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

/**
 * Created by dongqi on 15/6/7.
 */
@Service
public class OrderServiceImpl implements OrderService {
    private static final Logger log = Logger.getLogger(OrderServiceImpl.class.getName());

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Resource(name = "redisTemplate")
    private HashOperations<String, String, Object> hashOperations;

    @Autowired
    private CommonService commonService;

    @Override
    public Order save(Order order) {
        if (order.getId() != 0) {
            //update
        } else {
            //add
            order.setId(commonService.getSequenceNextValue(Order.class));
            String key = order.getClass().getSimpleName() + ":" + order.getId();
            for (Field f : order.getClass().getDeclaredFields()) {
                f.setAccessible(true);
                String hfield = f.getName();
                try {
                    Object hvalue = f.get(order);
                    if (hfield.equals("user")) {
                        User user = (User) hvalue;
                        hvalue = user.getName();
                    } else if (hfield.equals("foodItem")) {
                        FoodItem foodItem = (FoodItem) hvalue;
                        hvalue = foodItem.getId();
                    }
                    hashOperations.put(key, hfield, hvalue);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                f.setAccessible(false);
            }
        }
        return order;
    }

    @Override
    public List<Order> getOrders() {
        List<Order> orders = Lists.newArrayList();

        List<String> orderKeys = Lists.newArrayList();
        Set<String> keys = redisTemplate.keys(Order.class.getSimpleName() + "*");
        for(String key : keys) {
            orderKeys.add(key);
        }
        Collections.sort(orderKeys);

        for(String orderKey : orderKeys) {
            Set<String> hfields = hashOperations.keys(orderKey);
            for (String hfield: hfields) {

            }
        }

        return orders;
    }
}
