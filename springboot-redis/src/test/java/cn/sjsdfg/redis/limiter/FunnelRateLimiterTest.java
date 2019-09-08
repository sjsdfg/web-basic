package cn.sjsdfg.redis.limiter;

import cn.sjsdfg.redis.TestBase;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by Joe on 2019/9/8.
 */
public class FunnelRateLimiterTest extends TestBase {
    @Autowired
    private FunnelRateLimiter funnelRateLimiter;

    @Test
    public void test() {
        for (int i = 0; i < 20; i++) {
            System.out.println(funnelRateLimiter.isActionAllowed("funnel", "leak", 30, 0.5f));
        }
    }
}
