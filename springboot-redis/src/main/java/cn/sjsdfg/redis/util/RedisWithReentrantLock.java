package cn.sjsdfg.redis.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Created by Joe on 2019/6/14.
 * Redis 实现可重入锁，但是并不推荐使用
 * 原因是：加重了客户端的复杂性
 */
@Component
public class RedisWithReentrantLock {
    @Autowired
    private StringRedisTemplate redisTemplate;
    private ThreadLocal<Map<String, Integer>> lockers = new ThreadLocal<>();

    private Optional<Boolean> _lock(String key) {
        return Optional.ofNullable(redisTemplate.opsForValue()
                .setIfAbsent(key, "", 5, TimeUnit.SECONDS));
    }

    private Optional<Boolean> _unlock(String key) {
        return Optional.ofNullable(redisTemplate.delete(key));
    }

    private Map<String, Integer> currentLockers() {
        Map<String, Integer> refs = lockers.get();
        if (Objects.isNull(refs)) {
            refs = new HashMap<>();
            lockers.set(refs);
        }
        return refs;
    }

    public boolean lock(String key) {
        Map<String, Integer> ref = currentLockers();
        Integer refCount = ref.get(key);
        if (Objects.nonNull(refCount)) {
            ref.put(key, refCount + 1);
            return true;
        }
        Optional<Boolean> result = _lock(key);
        return result.map(success -> {
            if (success) {
                ref.put(key, 1);
            }
            return success;
        }).orElse(false);
    }

    public boolean unlock(String key) {
        Map<String, Integer> ref = currentLockers();
        Integer refCount = ref.get(key);
        if (Objects.isNull(refCount)) {
            return false;
        }
        refCount -= 1;
        if (refCount > 0) {
            ref.put(key, refCount);
        } else {
            ref.remove(key);
            _unlock(key);
        }
        return true;
    }
}
