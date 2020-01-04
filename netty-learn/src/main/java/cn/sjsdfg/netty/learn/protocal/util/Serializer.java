package cn.sjsdfg.netty.learn.protocal.util;

import cn.sjsdfg.netty.learn.protocal.enums.SerializerAlgorithm;

/**
 * @author Joe
 * @date 2020/1/4
 */
public interface Serializer {
    /**
     * 序列化算法
     *
     * @return
     */
    SerializerAlgorithm getSerializerAlgorithm();

    /**
     * java 对象转换成二进制
     *
     * @param object
     * @return
     */
    byte[] serialize(Object object);

    /**
     * 反序列化
     *
     * @param clazz
     * @param bytes
     * @param <T>
     * @return
     */
    <T> T deserialize(Class<T> clazz, byte[] bytes);
}
