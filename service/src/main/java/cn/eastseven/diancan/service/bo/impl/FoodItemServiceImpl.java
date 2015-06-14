package cn.eastseven.diancan.service.bo.impl;

import cn.eastseven.diancan.service.bo.CommonService;
import cn.eastseven.diancan.service.model.FoodItem;
import cn.eastseven.diancan.service.bo.FoodItemService;
import com.google.common.collect.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Set;

/**
 * Created by dongqi on 15/6/7.
 */
@Service
public class FoodItemServiceImpl implements FoodItemService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Resource(name = "redisTemplate")
    private HashOperations<String, String, Object> hashOperations;

    @Autowired
    private CommonService commonService;

    @Override
    public Object upload(String content) {
        final long createTime = Calendar.getInstance().getTimeInMillis();
        Set<FoodItem> foodItemSet = Sets.newHashSet();
        String[] rowdata = content.split(";");
        for (String data : rowdata) {
            String[] item = data.split(",");
            FoodItem foodItem = new FoodItem();
            long id = commonService.getSequenceNextValue(FoodItem.class);
            foodItem.setId(id);
            foodItem.setName(item[0].trim());
            foodItem.setPrice(Double.valueOf(item[1].trim()));
            foodItem.setValid(Boolean.TRUE);
            foodItem.setCreateTime(createTime);
            foodItemSet.add(foodItem);

            String foodItemKey = foodItem.getClass().getSimpleName() + ":" + id;
            BoundHashOperations operations = redisTemplate.boundHashOps(foodItemKey);
            for (Field f : foodItem.getClass().getDeclaredFields()) {
                f.setAccessible(true);
                String key = f.getName().trim();
                Object value = null;
                try {
                    value = f.get(foodItem);
                    operations.put(key, value);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                log.info(foodItemKey + ": " + key + ", " + value);
                f.setAccessible(false);
            }
        }
        log.info(Arrays.toString(foodItemSet.toArray()));
        return foodItemSet;
    }

    @Override
    public Set<FoodItem> getAll() {
        Set<FoodItem> foodItemSet = Sets.newHashSet();
        Set<String> keys = redisTemplate.keys(FoodItem.class.getSimpleName() + "*");
        log.info("getAll: " + keys.size());
        for (String key : keys) {
            log.info(key);
            FoodItem e = new FoodItem();
            for (Field f : e.getClass().getDeclaredFields()) {
                f.setAccessible(true);
                Object value = redisTemplate.boundHashOps(key).get(f.getName());
                log.info(key + ": " + f.getName() + ", " + value + ", " + f.getType() + ", " + f.getGenericType());
                try {
                    f.set(e, value);
                } catch (IllegalAccessException e1) {
                    e1.printStackTrace();
                }
                f.setAccessible(false);
            }

            if(e.getId() > 0) foodItemSet.add(e);
        }
        log.info(Arrays.toString(foodItemSet.toArray()));
        return foodItemSet;
    }

    @Override
    public FoodItem get(long id) {
        FoodItem foodItem = new FoodItem();
        String foodItemKey = FoodItem.class.getSimpleName() + ":" + id;
        for (Field f : foodItem.getClass().getDeclaredFields()) {
            f.setAccessible(true);
            Object value = hashOperations.get(foodItemKey, f.getName());
            try {
                f.set(foodItem, value);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            f.setAccessible(false);
        }
        return foodItem;
    }

    @Override
    public FoodItem modify(FoodItem foodItem) {
        String foodItemKey = foodItem.getClass().getSimpleName() + ":" + foodItem.getId();
        for (Field f : foodItem.getClass().getDeclaredFields()) {
            f.setAccessible(true);
            String hfield = f.getName();
            Object hvalue = null;
            try {
                hvalue = f.get(foodItem);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

            if(hvalue != null) {
                hashOperations.put(foodItemKey, hfield, hvalue);
            }

            f.setAccessible(false);
        }
        return foodItem;
    }
}
