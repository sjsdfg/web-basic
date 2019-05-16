package cn.sjsdfg.mongo.service;

import cn.sjsdfg.mongo.dto.PageResult;
import cn.sjsdfg.mongo.entity.User;
import cn.sjsdfg.mongo.helper.MongoPageHelper;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by Joe on 2019/5/16.
 */
@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class MongoHelperTest {
    @Autowired
    private MongoPageHelper pageHelper;

    @Test
    public void testPage() {
        Query query = new Query();
        PageResult<User> userPageResult = pageHelper.pageQuery(query, User.class, 10, 20);
        log.info("result = {}", userPageResult);
    }
}
