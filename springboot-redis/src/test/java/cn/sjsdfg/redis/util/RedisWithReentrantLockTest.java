package cn.sjsdfg.redis.util;

import cn.sjsdfg.redis.TestBase;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by Joe on 2019/6/14.
 */
public class RedisWithReentrantLockTest extends TestBase {
    @Autowired
    private RedisWithReentrantLock redis;

    @Test
    public void testLock() {
        System.out.println(redis.lock("codehole"));
        System.out.println(redis.lock("codehole"));
        System.out.println(redis.unlock("codehole"));
        System.out.println(redis.unlock("codehole"));
        System.out.println(redis.unlock("codehole"));
    }
}
