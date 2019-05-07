package cn.sjsdfg.redis.dao;

import cn.sjsdfg.redis.bean.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Created by Joe on 2019/5/6.
 */
@Repository
public class UserRedisDao {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public void add(User user) {
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        valueOperations.set(user.getId(), user);
    }

    public User getUser(String id) {
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        return (User) valueOperations.get(id);
    }

    public void addByLambda(User user) {
        redisTemplate.execute((RedisConnection connection) -> {
            RedisSerializer<Object> valueSerializer = (RedisSerializer<Object>) redisTemplate.getValueSerializer();
            RedisSerializer<String> keySerializer = redisTemplate.getStringSerializer();
            connection.set(keySerializer.serialize(user.getId()),
                    valueSerializer.serialize(user));
            return null;
        });
    }

    public User getUserByLambda(String id) {
        return redisTemplate.execute((RedisConnection connection) -> {
            RedisSerializer<String> keySerializer = redisTemplate.getStringSerializer();
            byte[] bytes = connection.get(keySerializer.serialize(id));
            RedisSerializer<Object> valueSerializer = (RedisSerializer<Object>) redisTemplate.getValueSerializer();
            return (User) valueSerializer.deserialize(bytes);
        });
    }

    public boolean deleteUser(String id) {
        Boolean result = redisTemplate.delete(id);
        return Objects.isNull(result) ? false : result;
    }

    public List<Object> queryAll() {
        return redisTemplate.executePipelined((RedisConnection redisConnection) -> {
            RedisSerializer<String> stringSerializer = redisTemplate.getStringSerializer();
            Set<String> keys = redisTemplate.keys("*");
            if (Objects.nonNull(keys)) {
                for (String key : keys) {
                    redisConnection.get(stringSerializer.serialize(key));
                }
            }
            return null;
        });
    }
}
