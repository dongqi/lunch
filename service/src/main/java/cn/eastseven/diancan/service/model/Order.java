package cn.eastseven.diancan.service.model;

import java.io.Serializable;
import java.util.Calendar;

/**
 * Created by dongqi on 15/6/7.
 */
public class Order implements Serializable, Comparable<Order> {

    private long id;
    private User user;
    private FoodItem foodItem;
    private long datetime = Calendar.getInstance().getTimeInMillis();
    private boolean pay = Boolean.FALSE;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public FoodItem getFoodItem() {
        return foodItem;
    }

    public void setFoodItem(FoodItem foodItem) {
        this.foodItem = foodItem;
    }

    public long getDatetime() {
        return datetime;
    }

    public void setDatetime(long datetime) {
        this.datetime = datetime;
    }

    public boolean isPay() {
        return pay;
    }

    public void setPay(boolean pay) {
        this.pay = pay;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", user=" + user +
                ", foodItem=" + foodItem +
                ", datetime=" + datetime +
                ", pay=" + pay +
                '}';
    }

    @Override
    public int compareTo(Order o) {
        return (int) (id - o.getId());
    }
}
