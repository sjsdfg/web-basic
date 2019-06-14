package cn.sjsdfg.redis.util;

import cn.sjsdfg.common.util.serializer.JsonSerializer;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.UUID;

/**
 * Created by Joe on 2019/6/14.
 * RedisDelayingQueue Redis 延时队列
 */
@Component
public class RedisDelayingQueue<T> {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private TypeReference<TaskItem<T>> taskType = new TypeReference<TaskItem<T>>() {
    };
    private String queueKey;

    public RedisDelayingQueue() {
        this.queueKey = "delay-queue-demo";
    }

    public RedisDelayingQueue(String queueKey) {
        this.queueKey = queueKey;
    }

    public void delay(T msg) {
        TaskItem<T> task = new TaskItem<>();
        task.setId(UUID.randomUUID().toString());
        task.setMsg(msg);
        try {
            String s = JsonSerializer.INSTANCE.serialize(task);
            stringRedisTemplate.opsForZSet().add(queueKey, s, System.currentTimeMillis() + 5000);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void loop() {
        while (!Thread.interrupted()) {
            Set<String> values = stringRedisTemplate.opsForZSet().rangeByScore(queueKey, 0, System.currentTimeMillis(), 0, 1);
            if (values.isEmpty()) {
                try {
                    Thread.sleep(500);
                } catch (Exception e) {
                }
                continue;
            }
            String s = values.iterator().next();
            if (stringRedisTemplate.opsForZSet().remove(queueKey, s) > 0) {
                try {
                    TaskItem<T> taskItem = JsonSerializer.INSTANCE.deserialize(s, taskType);
                    handleMsg(taskItem.getMsg());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public void handleMsg(T msg) {
        System.out.println(msg);
    }

    @Data
    private static class TaskItem<T> {
        private String id;
        private T msg;
    }
}
