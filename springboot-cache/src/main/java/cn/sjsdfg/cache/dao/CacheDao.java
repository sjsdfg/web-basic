package cn.sjsdfg.cache.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by Joe on 2019/5/5.
 */
@Repository
public class CacheDao {
    @Autowired
    private ApplicationContext context;
    private ConcurrentMapCacheManager cacheManager;
    private HashMap<String, String> map = new HashMap<>();

    @PostConstruct
    public void init() {
        cacheManager = context.getBean(ConcurrentMapCacheManager.class);
    }

    public void addDefaultVal() {
        for (int i = 0; i < 5; i++) {
            map.put(String.valueOf(i), String.valueOf(i));
        }
    }

    @Cacheable(value = "mapCache", key = "targetClass + methodName +#p0")
    public String findByKey(String key) {
        System.out.println("findByKey is called " + key);
        return map.getOrDefault(key, "empty");
    }

    public void showAllCaches() {
        Collection<String> cacheNames = cacheManager.getCacheNames();
        for (String cacheName : cacheNames) {
            System.out.println("-------- " + cacheName + " -----------");
            ConcurrentMapCache cache = (ConcurrentMapCache) cacheManager.getCache(cacheName);
            ConcurrentMap<Object, Object> nativeCache = cache.getNativeCache();
            nativeCache.forEach((key, value) -> System.out.println(key + " : " + value));
            System.out.println("-------- " + cacheName + " -----------");
        }
    }
}
