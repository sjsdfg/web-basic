package cn.sjsdfg.redis.service;

import cn.sjsdfg.redis.TestBase;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by Joe on 2019/5/8.
 */
public class LuaScriptServiceTest extends TestBase {
    @Autowired
    private LuaScriptService luaScriptService;

    @Test
    public void testRedisAddScriptExec() {
        luaScriptService.redisAddScriptExec();
    }
}
