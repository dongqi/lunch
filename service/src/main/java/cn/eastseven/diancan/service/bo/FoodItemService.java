package cn.eastseven.diancan.service.bo;

import cn.eastseven.diancan.service.model.FoodItem;

import java.util.List;
import java.util.logging.Logger;

/**
 * Created by dongqi on 15/6/7.
 */
public interface FoodItemService {
    final Logger log = Logger.getLogger(FoodItemService.class.getName());

    public Object upload(String content);

    public List<FoodItem> getAll();

    public FoodItem get(long id);

    public FoodItem modify(FoodItem foodItem);
}
