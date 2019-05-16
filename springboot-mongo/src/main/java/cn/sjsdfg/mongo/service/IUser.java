package cn.sjsdfg.mongo.service;

import cn.sjsdfg.mongo.entity.User;

import java.util.List;

/**
 * Created by Joe on 2019/4/28.
 */
public interface IUser {
    void saveUser(User user);

    void batchInsertUser(List<User> users);

    User findUserByName(String name);

    void removeUser(String name);

    void updateUser(String name, String key, String value);

    List<User> listUser();
}
