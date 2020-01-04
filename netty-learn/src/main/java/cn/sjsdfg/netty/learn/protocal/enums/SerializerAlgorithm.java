package cn.sjsdfg.netty.learn.protocal.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Joe
 * @date 2020/1/4
 */
@Getter
@AllArgsConstructor
public enum SerializerAlgorithm {
    /**
     * json 序列化
     */
    Json(1);

    private int algorithm;

    public static SerializerAlgorithm parse(int algorithm) {
        for (SerializerAlgorithm value : SerializerAlgorithm.values()) {
            if (value.getAlgorithm() == algorithm) {
                return value;
            }
        }
        return null;
    }
}
