package cn.eastseven.diancan.service.bo;


import cn.eastseven.diancan.service.model.User;

/**
 * Created by dongqi on 15/6/7.
 */
public interface UserService {

    public User get(String username);

    public User save(User user);

    public void saveHost(String host, String name);

    public String getName(String host);
}
