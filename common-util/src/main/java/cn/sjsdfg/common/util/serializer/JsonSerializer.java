package cn.sjsdfg.common.util.serializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Created by Joe on 2019/6/14.
 */
public class JsonSerializer implements Serializer {
    public static final JsonSerializer INSTANCE = new JsonSerializer();

    public static final ObjectMapper MAPPER = new ObjectMapper();

    private static ThreadLocal<DateFormat> sensorDataDateFormat = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        }
    };

    @PostConstruct
    public void init() {
        // SerializationFeature for changing how JSON is written

        // to enable standard indentation ("pretty-printing"):
        // OBJECT_MAPPER.enable(SerializationFeature.INDENT_OUTPUT);
        // to allow serialization of "empty" POJOs (no fields to serialize)
        // (without this setting, an exception is thrown in those cases)
        MAPPER.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);

        // DeserializationFeature for changing how JSON is read as POJOs:

        // to prevent exception when encountering unknown property:
        MAPPER.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        // to allow coercion of JSON empty String ("") to null Object value:
        MAPPER.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
    }

    @Override
    public <T> T deserialize(byte[] jsonBytes, Class<T> t) throws IOException {
        return MAPPER.readValue(jsonBytes, t);
    }

    @Override
    public <T> T deserialize(byte[] jsonBytes, TypeReference<T> t) throws IOException {
        return MAPPER.readValue(jsonBytes, t);
    }

    @Override
    public <T> T deserialize(String jsonString, Class<T> t) throws IOException {
        return MAPPER.readValue(jsonString, t);
    }

    @Override
    public <T> T deserialize(String jsonString, TypeReference<T> t) throws IOException {
        return MAPPER.readValue(jsonString, t);
    }

    @Override
    public String serialize(Object t) throws JsonProcessingException {
        if (t == null) {
            return StringUtils.EMPTY;
        }
        return MAPPER.writeValueAsString(t);
    }

    @Override
    public String serializeWithPretty(Object t) throws JsonProcessingException {
        if (t == null) {
            return StringUtils.EMPTY;
        }
        return MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(t);
    }

    /**
     * serialize object to string with special date format
     *
     * @param t
     * @return
     * @throws JsonProcessingException
     */
    @Override
    public String serializeSensorData(Object t) throws JsonProcessingException {
        ObjectMapper dateObjectMapper = new ObjectMapper();
        dateObjectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        dateObjectMapper.setDateFormat(sensorDataDateFormat.get());

        if (t == null) {
            return StringUtils.EMPTY;
        }
        return dateObjectMapper.writeValueAsString(t);
    }
}
