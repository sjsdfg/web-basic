package cn.sjsdfg.redis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Joe on 2019/5/8.
 */
@Service
public class LuaScriptService {
    @Autowired
    @Qualifier("customRedisTemplate")
    private RedisTemplate<String, Object> redisTemplate;

    private DefaultRedisScript<List> getRedisScript;

    @PostConstruct
    public void init() {
        getRedisScript = new DefaultRedisScript<List>();
        getRedisScript.setResultType(List.class);
        getRedisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("luascript/LimitLoadTimes.lua")));
    }

    public void redisAddScriptExec() {
        /**
         * List设置lua的KEYS
         */
        List<String> keyList = new ArrayList<>();
        keyList.add("count");
        keyList.add("rate.limiting:127.0.0.1");

        /**
         * 用Mpa设置Lua的ARGV[1]
         */
        Map<String, Object> argvMap = new HashMap<String, Object>();
        argvMap.put("expire", 10000);
        argvMap.put("times", 10);

        /**
         * 调用脚本并执行
         */
        List result = redisTemplate.execute(getRedisScript, keyList, argvMap);
        System.out.println(result);
    }
}
