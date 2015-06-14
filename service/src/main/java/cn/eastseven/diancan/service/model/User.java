package cn.eastseven.diancan.service.model;

import com.google.common.collect.Sets;

import java.util.Set;

/**
 * Created by dongqi on 15/6/7.
 */
public class User {

    private String name;

    private Set<String> ips = Sets.newHashSet();

    public User(String name) {
        this.name = name;
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
                "name='" + name + '\'' +
                ", ips=" + ips +
                '}';
    }
}
