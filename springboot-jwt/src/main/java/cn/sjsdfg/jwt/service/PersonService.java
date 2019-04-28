package cn.sjsdfg.jwt.service;

import cn.sjsdfg.jwt.bean.Person;

/**
 * Created by Joe on 2019/4/28.
 */
public interface PersonService {
    Person save(Person person);

    Person findByName(String name);
}
