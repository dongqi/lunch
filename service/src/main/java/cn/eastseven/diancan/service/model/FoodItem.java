package cn.eastseven.diancan.service.model;

import java.io.Serializable;
import java.util.Calendar;

/**
 * Created by dongqi on 15/6/7.
 */
public class FoodItem implements Serializable, Comparable<FoodItem> {

    private long id;
    private String name;
    private double price = 0.0D;
    private long createTime = Calendar.getInstance().getTimeInMillis();
    private boolean valid = Boolean.TRUE;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    @Override
    public String toString() {
        return "FoodItem{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", createTime=" + createTime +
                ", valid=" + valid +
                '}';
    }

    @Override
    public int compareTo(FoodItem o) {
        return (int) (id - o.getId());
    }
}
