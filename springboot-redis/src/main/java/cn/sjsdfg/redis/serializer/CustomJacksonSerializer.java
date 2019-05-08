package cn.sjsdfg.redis.serializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import java.nio.charset.Charset;
import java.util.Optional;

/**
 * Created by Joe on 2019/5/8.
 */
public class CustomJacksonSerializer<T> implements RedisSerializer<T> {
    public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");
    private Class<T> clazz;
    private ObjectMapper objectMapper = new ObjectMapper();

    public CustomJacksonSerializer(Class<T> clazz) {
        super();
        this.clazz = clazz;
    }

    @Override
    public byte[] serialize(T t) throws SerializationException {
        return Optional.ofNullable(t)
                .map(r -> {
                    try {
                        return objectMapper.writeValueAsString(t).getBytes(DEFAULT_CHARSET);
                    } catch (Exception e) {
                        throw new RuntimeException(e.getMessage());
                    }
                })
                .orElseGet(() -> new byte[0]);
    }

    @Override
    public T deserialize(byte[] bytes) throws SerializationException {
        return Optional.ofNullable(bytes)
                .map(t -> {
                    try {
                        return objectMapper.readValue(t, clazz);
                    } catch (Exception e) {
                        throw new RuntimeException(e.getMessage());
                    }
                })
                .orElseGet(() -> null);
    }
}
