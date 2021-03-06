package cn.sjsdfg.mongo.service;

import cn.sjsdfg.mongo.entity.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Joe on 2019/4/28.
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class UserServiceTest {
    @Autowired
    private IUser userService;

    @Test
    public void testInsert() {
        List<User> users = new ArrayList<>();
        for (int i = 0; i < 3000; i++) {
            User user = new User();
            user.setName(String.format("name%d", i));
            user.setAge(i);
            user.setAddress("test" + i);
            users.add(user);
        }
        userService.batchInsertUser(users);
    }

    @Test
    public void testSaveUser() {
        User user = new User();
        user.setName("小明");
        user.setAge(123);
        user.setAddress("test");
        userService.saveUser(user);
    }

    @Test
    public void testFindByName() {
        User user = userService.findUserByName("小明");
        System.out.println(user);
    }
}
