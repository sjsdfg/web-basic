package cn.sjsdfg.mongo.service.impl;

import cn.sjsdfg.mongo.entity.User;
import cn.sjsdfg.mongo.service.IUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Joe on 2019/4/28.
 */
@Service
public class UserServiceImpl implements IUser {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public void saveUser(User user) {
        mongoTemplate.save(user);
    }

    @Override
    public User findUserByName(String name) {
        return mongoTemplate.findOne(new Query(Criteria.where("name").is(name)), User.class);
    }

    @Override
    public void removeUser(String name) {
        mongoTemplate.remove(new Query(Criteria.where("name").is(name)), User.class);
    }

    @Override
    public void updateUser(String name, String key, String value) {
        mongoTemplate.updateFirst(new Query(Criteria.where("name").is(name)),
                Update.update(key, value)
                , User.class);
    }

    @Override
    public List<User> listUser() {
        return mongoTemplate.findAll(User.class);
    }
}
