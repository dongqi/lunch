package cn.eastseven.diancan.service.bo.impl;

import cn.eastseven.diancan.service.dao.BaseDao;
import cn.eastseven.diancan.service.model.FoodItem;
import cn.eastseven.diancan.service.bo.FoodItemService;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

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
    private BaseDao dao;

    @Override
    public Object upload(String content) {
        final long createTime = Calendar.getInstance().getTimeInMillis();
        List<FoodItem> foodItemSet = Lists.newArrayList();
        String[] rowdata = content.split(";");
        for (String data : rowdata) {
            String[] item = data.split(",");
            FoodItem foodItem = new FoodItem();
            foodItem.setName(item[0].trim());
            foodItem.setPrice(Double.valueOf(item[1].trim()));
            foodItem.setValid(Boolean.TRUE);
            foodItem.setCreateTime(createTime);

            dao.create(foodItem);
        }

        foodItemSet.addAll((Collection<FoodItem>) dao.all(FoodItem.class));
        Collections.sort(foodItemSet);
        log.info(Arrays.toString(foodItemSet.toArray()));
        return foodItemSet;
    }

    @Override
    public List<FoodItem> getAll() {
        List<FoodItem> foodItemSet = Lists.newArrayList();
        foodItemSet.addAll((Collection<FoodItem>) dao.all(FoodItem.class));
        Collections.sort(foodItemSet);

        return foodItemSet;
    }

    @Override
    public FoodItem get(long id) {
        FoodItem foodItem = (FoodItem) dao.retrieve(id, FoodItem.class);
        return foodItem;
    }

    @Override
    public FoodItem modify(FoodItem foodItem) {
        dao.update(foodItem);
        return foodItem;
    }
}
