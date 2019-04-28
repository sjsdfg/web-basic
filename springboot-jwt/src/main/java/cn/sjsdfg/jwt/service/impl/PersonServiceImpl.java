package cn.sjsdfg.jwt.service.impl;

import cn.sjsdfg.jwt.bean.Person;
import cn.sjsdfg.jwt.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

/**
 * Created by Joe on 2019/4/28.
 */
@Service
public class PersonServiceImpl implements PersonService {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public Person save(Person person) {
        return mongoTemplate.save(person);
    }

    @Override
    public Person findByName(String name) {
        return mongoTemplate.findOne(new Query(Criteria.where("username").is(name)), Person.class);
    }
}
