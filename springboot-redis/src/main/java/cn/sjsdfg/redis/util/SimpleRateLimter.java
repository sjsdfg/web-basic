package cn.sjsdfg.redis.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by Joe on 2019/9/8.
 */
@Component
public class SimpleRateLimter {
    @Autowired
    private StringRedisTemplate redisTemplate;

    public boolean isActionAllowed(String userId, String actionKey, int period, int maxCount) {
        String key = String.format("hist:%s:%s", userId, actionKey);
        long nowTs = System.currentTimeMillis();
        List<Object> results = redisTemplate.executePipelined((RedisConnection redisConnection) -> {
            RedisSerializer<String> stringSerializer = redisTemplate.getStringSerializer();
            byte[] keys = stringSerializer.serialize(key);
            redisConnection.zAdd(keys, nowTs, stringSerializer.serialize("" + nowTs));
            redisConnection.zRemRangeByScore(keys, 0, nowTs - period * 1000);
            redisConnection.zCard(keys);
            return null;
        });
        if (results.isEmpty()) {
            return true;
        }
        Long count = (Long) results.get(2);
        return count < maxCount;
    }
}
