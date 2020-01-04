package cn.sjsdfg.netty.learn.protocal.util;

import cn.sjsdfg.netty.learn.protocal.enums.SerializerAlgorithm;
import com.alibaba.fastjson.JSON;

/**
 * @author Joe
 * @date 2020/1/4
 */
public class JsonSerializer implements Serializer {

    @Override
    public SerializerAlgorithm getSerializerAlgorithm() {
        return SerializerAlgorithm.Json;
    }

    @Override
    public byte[] serialize(Object object) {
        return JSON.toJSONBytes(object);
    }

    @Override
    public <T> T deserialize(Class<T> clazz, byte[] bytes) {
        return JSON.parseObject(bytes, clazz);
    }
}
