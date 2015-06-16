package cn.eastseven.diancan.service;

import cn.eastseven.diancan.service.dao.BaseDao;
import cn.eastseven.diancan.service.model.FoodItem;
import cn.eastseven.diancan.service.model.Order;
import cn.eastseven.diancan.service.model.User;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.Collection;
import java.util.logging.Logger;

/**
 * Created by dongqi on 15/6/14.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "file:src/main/resources/redis.xml")
public class BaseDaoTests {

    private Logger log = Logger.getLogger(getClass().getName());

    @Autowired
    private BaseDao orderDao;

    @Before
    public void setup() {
        Collection foodItems = orderDao.all(FoodItem.class);
        if(foodItems == null || foodItems.isEmpty()) {
            FoodItem foodItem = new FoodItem();
            foodItem.setName("糖醋里脊");
            foodItem.setPrice(9.98D);
            long id = orderDao.create(foodItem);
            log.info(id+": "+foodItem.toString());
        } else {
            log.info(Arrays.toString(foodItems.toArray()));
        }
    }

    @After
    public void teardown() {
        Collection foodItems = orderDao.all(FoodItem.class);
        if(!foodItems.isEmpty()) {
            orderDao.deleteAll(FoodItem.class);
        }
    }

    @Test
    public void testGetNextID() {
        long id = orderDao.getNextID(Order.class);
        Assert.assertTrue(id > 0);
        log.info("getNextID: " + id);
    }

    @Test
    public void testCreate() {
        Collection foodItems = orderDao.all(FoodItem.class);
        Assert.assertNotNull(foodItems);
        User user = new User("dongqi");
        orderDao.create(user);
        FoodItem foodItem = (FoodItem) foodItems.iterator().next();

        Order order = new Order();
        order.setUser(user);
        order.setFoodItem(foodItem);

        long id = orderDao.create(order);
        Assert.assertTrue(id > 0);
        log.info(order.toString());
    }

    @Test
    public void testGetAll() {
        testCreate();
        Collection orders = orderDao.all(Order.class);
        int index = 1;
        for (Object order : orders) {
            log.info("testGetAll\t"+index + ": " + order.toString());
            index++;
        }
        Assert.assertNotNull(orders);
        Assert.assertTrue(orders.size() > 0);
    }

    @Test
    public void testRetrieve() {
        Object order = orderDao.retrieve(1, Order.class);
        Assert.assertNotNull(order);
        log.info(order.toString());
    }
}
