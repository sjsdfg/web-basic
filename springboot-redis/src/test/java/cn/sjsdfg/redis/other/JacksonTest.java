package cn.sjsdfg.redis.other;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Joe on 2019/5/8.
 */
public class JacksonTest {
    @Test
    public void testJackson() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> argvMap = new HashMap<>();
        argvMap.put("expire", 10000);
        argvMap.put("times", 10);
        System.out.println();
        byte[] bytes = objectMapper.writeValueAsString(argvMap).getBytes();
        Object o = objectMapper.readValue(bytes, Object.class);
        System.out.println(o);
    }
}
