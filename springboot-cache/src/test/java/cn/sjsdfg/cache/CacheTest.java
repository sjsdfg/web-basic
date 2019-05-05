package cn.sjsdfg.cache;

import cn.sjsdfg.cache.dao.CacheDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by Joe on 2019/5/5.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CacheApplication.class)
public class CacheTest {
    @Autowired
    CacheDao cacheDao;
    @Autowired
    ApplicationContext context;

    @Test
    public void testFindByKey() {
        cacheDao.addDefaultVal();
        System.out.println(cacheDao.findByKey("1"));
        System.out.println(cacheDao.findByKey("1"));

        System.out.println(cacheDao.findByKey("2"));
        System.out.println(cacheDao.findByKey("2"));

        System.out.println(cacheDao.findByKey("1"));

        cacheDao.showAllCaches();
    }
}
