package cn.sjsdfg.common.util.serializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.IOException;

public interface Serializer {

    String serialize(Object obj) throws JsonProcessingException;

    String serializeWithPretty(Object obj) throws JsonProcessingException;

    <T> T deserialize(byte[] input, Class<T> clazz) throws IOException;

    <T> T deserialize(byte[] jsonBytes, TypeReference<T> t) throws IOException;

    <T> T deserialize(String jsonString, Class<T> t) throws IOException;

    <T> T deserialize(String jsonString, TypeReference<T> t) throws IOException;

    String serializeSensorData(Object t) throws JsonProcessingException;
}
