package cn.sjsdfg.redis.limiter;

import cn.sjsdfg.redis.TestBase;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by Joe on 2019/9/8.
 */
public class SimpleRateLimiterTest extends TestBase {
    @Autowired
    private SimpleRateLimiter simpleRateLimter;

    @Test
    public void test() {
        for (int i = 0; i < 20; i++) {
            System.out.println("-------------");
            System.out.println(simpleRateLimter.isActionAllowed("laoqian", "reply", 60, 5));
            System.out.println("-------------");
        }
    }
}
