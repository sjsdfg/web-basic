package cn.sjsdfg.redis.dao;

import cn.sjsdfg.redis.TestBase;
import cn.sjsdfg.redis.bean.User;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created by Joe on 2019/5/6.
 */
public class UserRedisDaoTest extends TestBase {
    @Autowired
    private UserRedisDao userRedisDao;

    @Test
    public void testAddAndGet() {
        User user = new User("1", "1", "1");
        userRedisDao.add(user);

        System.out.println(userRedisDao.getUser("1"));
    }

    @Test
    public void testAddAndGetLambda() {
        User user = new User("1", "1", "1");
        userRedisDao.addByLambda(user);

        System.out.println(userRedisDao.getUserByLambda("1"));
    }

    @Test
    public void testDelete() {
        User user = new User("2", "2", "2");
        userRedisDao.add(user);
        System.out.println(userRedisDao.getUser("2"));
        System.out.println("-----删除元素------");
        userRedisDao.deleteUser("2");
        System.out.println(userRedisDao.getUser("2"));
    }

    @Test
    public void testQueryAll() {
        List<Object> result = userRedisDao.queryAll();
        result.forEach(System.out::println);
    }
}
