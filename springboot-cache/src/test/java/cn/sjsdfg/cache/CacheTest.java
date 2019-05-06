package cn.sjsdfg.cache;

import cn.sjsdfg.cache.dao.CacheDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by Joe on 2019/5/5.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CacheApplication.class)
public class CacheTest {
    @Autowired
    private CacheDao cacheDao;

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

    @Test
    public void testUpdateByKey() {
        cacheDao.addDefaultVal();
        System.out.println(cacheDao.findByKey("1"));
        System.out.println(cacheDao.findByKey("1"));
        cacheDao.showAllCaches();
        cacheDao.updateByKey("1", "123");
        System.out.println(cacheDao.findByKey("1"));
        cacheDao.showAllCaches();
    }

    @Test
    public void testDeleteOneByKey() {
        cacheDao.addDefaultVal();
        for (int i = 0; i < 5; i++) {
            cacheDao.findByKey(String.valueOf(i));
        }
        cacheDao.showAllCaches();
        System.out.println("====调用删除缓存====");
        cacheDao.deleteOneByKey("1");
        cacheDao.showAllCaches();
    }

    @Test
    public void testDeleteAll() {
        cacheDao.addDefaultVal();
        for (int i = 0; i < 5; i++) {
            cacheDao.findByKey(String.valueOf(i));
        }
        cacheDao.showAllCaches();
        System.out.println("====调用清空所有缓存====");
        cacheDao.deleteAll();
        cacheDao.showAllCaches();
    }
}
