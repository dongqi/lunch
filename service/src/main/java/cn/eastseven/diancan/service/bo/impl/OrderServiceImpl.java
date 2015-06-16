package cn.eastseven.diancan.service.bo.impl;

import cn.eastseven.diancan.service.bo.CommonService;
import cn.eastseven.diancan.service.bo.OrderService;
import cn.eastseven.diancan.service.dao.BaseDao;
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
import java.util.Collection;
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
    private BaseDao dao;

    @Override
    public Order save(Order order) {
        if (order.getId() != 0) {
            //update
            return (Order) dao.update(order);
        } else {
            //add
            dao.create(order);
        }
        return order;
    }

    @Override
    public List<Order> getOrders() {
        List<Order> orders = Lists.newArrayList();

        Collection<Order> all = (Collection<Order>) dao.all(Order.class);
        orders.addAll(all);
        Collections.sort(orders);

        return orders;
    }

    @Override
    public Order get(long id) {
        return (Order) dao.retrieve(id, Order.class);
    }

    @Override
    public int delete(long id) {
        dao.delete(id, Order.class);
        return 0;
    }
}
