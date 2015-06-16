package cn.eastseven.diancan.service.model;

import com.google.common.collect.Sets;

import java.io.Serializable;
import java.util.Set;

/**
 * Created by dongqi on 15/6/7.
 */
public class User implements Serializable {

    private long id;
    private String name;

    private Set<String> ips = Sets.newHashSet();

    public User() {}

    public User(String name) {
        this.name = name;
    }

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

    public Set<String> getIps() {
        return ips;
    }

    public void setIps(Set<String> ips) {
        this.ips = ips;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", ips=" + ips +
                '}';
    }
}
