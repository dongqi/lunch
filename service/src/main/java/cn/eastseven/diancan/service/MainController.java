package cn.eastseven.diancan.service;

import cn.eastseven.diancan.service.bo.OrderService;
import cn.eastseven.diancan.service.bo.UserService;
import cn.eastseven.diancan.service.model.FoodItem;
import cn.eastseven.diancan.service.model.Order;
import cn.eastseven.diancan.service.model.User;
import cn.eastseven.diancan.service.bo.FoodItemService;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by dongqi on 15/6/7.
 */
@RestController
public class MainController {
    private static Logger log = Logger.getLogger(MainController.class.getName());

    @Autowired
    private FoodItemService foodItemService;

    @Autowired
    private UserService userService;

    @Autowired
    private OrderService orderService;

    @RequestMapping(method = RequestMethod.GET, value = "/login")
    public User login(@RequestParam(required = false, value = "account", defaultValue = "") String name, HttpServletRequest req, HttpServletResponse res) {
        log.info("header:"+req.getHeader("host")+", "+req.getHeader("x-real-ip"));

        String host = req.getHeader("x-real-ip");
        if(StringUtils.isEmpty(name)) {
            name = userService.getName(host);
        }
        User user = userService.get(name);
        log.info(user.toString());
        return user;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/signin")
    public void signin(@RequestParam(value = "account") String username, HttpServletRequest req) {
        User user = new User(username);
        String host = req.getHeader("x-real-ip");
        user.getIps().add(host);
        userService.save(user);
        userService.saveHost(host, username);
        log.info(host+": "+username);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/upload")
    public Object upload(@RequestParam(value = "menuContent") String content) {
        log.info(content);
        if(StringUtils.isEmpty(content)) return null;
        return foodItemService.upload(content);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/list")
    public List<FoodItem> getAllFoodItems() {
        return foodItemService.getAll();
    }

    @RequestMapping(method = RequestMethod.POST, value = "/ordering")
    public FoodItem order(@RequestParam(value = "id") long id, @RequestParam(value = "username") String username, HttpServletRequest req) {
        userService.saveHost(req.getRemoteHost(), username);
        User user = userService.get(username);
        user.getIps().add(req.getRemoteHost());

        FoodItem foodItem = foodItemService.get(id);

        Order order = new Order();
        order.setUser(user);
        order.setFoodItem(foodItem);

        order = orderService.save(order);
        log.info(""+order);
        return foodItem;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/setvalid")
    public FoodItem modify(@RequestParam(value = "id") long id, @RequestParam(value = "valid") boolean valid) {
        FoodItem foodItem = foodItemService.get(id);
        foodItem.setValid(valid);
        foodItem = foodItemService.modify(foodItem);

        return foodItem;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/pay")
    public Order orderPay(@RequestParam(value = "id") long id, @RequestParam(value = "pay") boolean pay) {
        Order order = orderService.get(id);
        order.setPay(pay);
        orderService.save(order);

        return order;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/del")
    public int orderDelete(@RequestParam(value = "id") long id) {
        return orderService.delete(id);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/currentOrders")
    public List<Order> getOrders() {
        List<Order> orders = Lists.newArrayList();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String today = sdf.format(new Date());
        for(Order order : orderService.getOrders()) {
            String time = sdf.format(new Date(order.getDatetime()));
            if(!today.equalsIgnoreCase(time)) continue;
            orders.add(order);
        }

        return orders;
    }
}
