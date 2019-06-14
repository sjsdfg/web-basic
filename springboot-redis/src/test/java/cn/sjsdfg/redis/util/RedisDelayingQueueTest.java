package cn.sjsdfg.redis.util;

import cn.sjsdfg.redis.TestBase;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by Joe on 2019/6/15.
 */
public class RedisDelayingQueueTest extends TestBase {
    @Autowired
    private RedisDelayingQueue<String> queue;

    @Test
    public void test() {
        Thread producer = new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                queue.delay("codehole" + i);
            }
        });
        Thread consumer = new Thread(() -> queue.loop());
        producer.start();
        consumer.start();
        try {
            producer.join();
            Thread.sleep(6000);
            consumer.interrupt();
            consumer.join();
        } catch (InterruptedException e) {
        }
    }
}
