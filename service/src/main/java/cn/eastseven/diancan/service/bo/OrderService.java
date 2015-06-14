package cn.eastseven.diancan.service.bo;


import cn.eastseven.diancan.service.model.Order;

import java.util.List;

/**
 * Created by dongqi on 15/6/7.
 */
public interface OrderService {

    public Order save(Order order);

    public List<Order> getOrders();
}
