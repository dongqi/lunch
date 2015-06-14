package cn.eastseven.diancan.service.bo;

import cn.eastseven.diancan.service.model.FoodItem;

import java.util.Set;
import java.util.logging.Logger;

/**
 * Created by dongqi on 15/6/7.
 */
public interface FoodItemService {
    final Logger log = Logger.getLogger(FoodItemService.class.getName());

    public Object upload(String content);

    public Set<FoodItem> getAll();

    public FoodItem get(long id);

    public FoodItem modify(FoodItem foodItem);
}
