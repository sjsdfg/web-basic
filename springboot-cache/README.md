# Spring Boot Cache

## Spring 缓存抽象

Spring 从 3.1 开始定义了 org.springframework.cache.Cache 和 org.springframework.cache.CacheManager 接口来统一不同的缓存技术；并支持使用 JCache（JSR-107）注解简化我们开发；

- Cache 接口为缓存的组件规范定义，包含缓存的各种操作集合；
- Cache 接口下 Spring 提供了各种 xxxCache 的实现；如 RedisCache，EhCacheCache ，ConcurrentMapCache 等；
- 每次调用需要缓存功能的方法时，Spring 会检查检查指定参数的指定的目标方法是否已经被调用过；如果有就直接从缓存中获取方法调用后的结果，如果没有就调用方法并缓存结果后返回给用户。下次调用直接从缓存中获取。
- 使用 Spring 缓存抽象时我们需要关注以下两点；
  1. 确定方法需要被缓存以及他们的缓存策略
  2. 从缓存中读取之前缓存存储的数据

## 重要概念&缓存注解

| 名称           | 解释                                                         |
| -------------- | ------------------------------------------------------------ |
| Cache          | 缓存接口，定义缓存操作。实现有：RedisCache、EhCacheCache、ConcurrentMapCache 等 |
| CacheManager   | 缓存管理器，管理各种缓存（cache）组件                        |
| @Cacheable     | 主要针对方法配置，能够根据方法的请求参数对其进行缓存         |
| @CacheEvict    | 清空缓存                                                     |
| @CachePut      | 保证方法被调用，又希望结果被缓存。 <br> 与@Cacheable 区别在于是否每次都调用方法，常用于更新 |
| @EnableCaching | 开启基于注解的缓存                                           |
| keyGenerator   | 缓存数据时 key 生成策略                                        |
| serialize      | 缓存数据时 value 序列化策略                                    |
| @CacheConfig   | 统一配置本类的缓存注解的属性                                 |

**@Cacheable/@CachePut/@CacheEvict 主要的参数**

| 名称                           | 解释                                                         |
| ------------------------------ | ------------------------------------------------------------ |
| value                          | 缓存的名称，在 spring 配置文件中定义，必须指定至少一个 <br/> 例如： <br/>@Cacheable(value=”mycache”)  或者 <br/>@Cacheable(value={”cache1”,”cache2”} |
| key                            | 缓存的 key，可以为空，如果指定要按照 SpEL 表达式编写， 如果不指定，则缺省按照方法的所有参数进行组合 <br/> 例如： <br/>@Cacheable(value=”testcache”,key=”#id”) |
| condition                      | 缓存的条件，可以为空，使用 SpEL 编写，返回 true 或者 false， 只有为 true 才进行缓存/清除缓存 <br/> 例如：<br/>@Cacheable(value=”testcache”, condition=”#userName.length()>2”) |
| unless                         | 否定缓存。当条件结果为 TRUE 时，就不会缓存。 <br/>@Cacheable(value=”testcache”,unless=”#userName.length()>2”) |
| allEntries (@CacheEvict)      | 是否清空所有缓存内容，缺省为 false，如果指定为 true， 则方法调用后将立即清空所有缓存 <br/> 例如：<br/> @CachEvict(value=”testcache”,allEntries=true) |
| beforeInvocation (@CacheEvict) | 是否在方法执行前就清空，缺省为 false，如果指定为 true， 则在方法还没有执行的时候就清空缓存，缺省情况下，如果方法 执行抛出异常，则不会清空缓存 <br/> 例如： <br/>@CachEvict(value=”testcache”，beforeInvocation=true) |

**注意：**

1. 当我们要使用 root 对象的属性作为 key 时我们也可以将“#root”省略，因为 Spring 默认使用的就是 root 对象的属性。 如 `@Cacheable(key = "targetClass + methodName +#p0")`
2. 使用方法参数时我们可以直接使用 “#参数名” 或者 “#p 参数 index”。 如：
   1. `@Cacheable(value="users", key="#id")`
   2. `@Cacheable(value="users", key="#p0")`

**SpEL 提供了多种运算符**

| **类型**   | **运算符**                                     |
| ---------- | ---------------------------------------------- |
| 关系       | <，>，<=，>=，==，!=，lt，gt，le，ge，eq，ne   |
| 算术       | +，- ，* ，/，%，^                             |
| 逻辑       | &&，\|\|，!，and，or，not，between，instanceof |
| 条件       | ?: (ternary)，?: (elvis)                       |
| 正则表达式 | matches                                        |
| 其他类型   | ?.，?[…]，![…]，^[…]，$[…]                     |

## 使用示例

### 1. 依赖

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-cache</artifactId>
</dependency>
```

### 2. 开启缓存

在启动类注解 **@EnableCaching** 开启缓存

```java
package cn.sjsdfg.cache;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * Created by Joe on 2019/5/5.
 */
@EnableCaching
@SpringBootApplication
public class CacheApplication {
    public static void main(String[] args) {
        SpringApplication.run(CacheApplication.class, args);
    }
}

```

因为我们没有集成第三方缓存，采用的是 Spring 自带的缓存，其中底层的实现是 `ConcurrentMapCache`。

因而我们实现了一个方法 `cn.sjsdfg.cache.dao.CacheDao#showAllCaches`，可以用于查看所有已经缓存的键值对情况。

```java
public void showAllCaches() {
    Collection<String> cacheNames = cacheManager.getCacheNames();
    for (String cacheName : cacheNames) {
        System.out.println("-------- " + cacheName + " -----------");
        ConcurrentMapCache cache = (ConcurrentMapCache)cacheManager.getCache(cacheName);
        ConcurrentMap<Object, Object> nativeCache = cache.getNativeCache();
        nativeCache.forEach((key, value) -> System.out.println(key + " : " + value));
        System.out.println("-------- " + cacheName + " -----------");
    }
}
```

### 3.缓存@Cacheable

`@Cacheable`注解会先查询是否已经有缓存，有会使用缓存，没有则会执行方法并缓存。

```java
@Cacheable(value = "mapCache", key = "targetClass + methodName +#p0")
public String findByKey(String key) {
    System.out.println("findByKey is called " + key);
    return map.getOrDefault(key, "empty");
}
```

此处的`value`是必需的，它指定了你的缓存存放在哪块命名空间。

此处的`key`是使用的spEL表达式，参考上章。这里有一个小坑，如果你把`methodName`换成`method`运行会报错，观察它们的返回类型，原因在于`methodName`是`String`而`methoh`是`Method`。

调用方法的参数必须实现 `Serializable` 接口，否则会报`java.io.NotSerializableException`异常。

到这里，你已经可以运行程序 `cn.sjsdfg.cache.CacheTest#testFindByKey` 检验缓存功能是否实现。

测试输出为：

```java
findByKey is called 1
1
1
findByKey is called 2
2
2
1
-------- mapCache -----------
class cn.sjsdfg.cache.dao.CacheDaofindByKey2 : 2
class cn.sjsdfg.cache.dao.CacheDaofindByKey1 : 1
-------- mapCache -----------
```

**深入源码，查看它的其它属性**

我们打开`@Cacheable`注解的源码，可以看到该注解提供的其他属性，如：

```java
String[] cacheNames() default {}; //和value注解差不多，二选一
String keyGenerator() default ""; //key的生成器。key/keyGenerator二选一使用
String cacheManager() default ""; //指定缓存管理器
String cacheResolver() default ""; //或者指定获取解析器
String condition() default ""; //条件符合则缓存
String unless() default ""; //条件符合则不缓存
boolean sync() default false; //是否使用异步模式
```

### 4. 配置@CacheConfig

当我们需要缓存的地方越来越多，你可以使用 `@CacheConfig(cacheNames = {"myCache"})` 注解来统一指定 `value` 的值，这时可省略 `value`，如果你在你的方法依旧写上了 `value` ，那么依然以方法的 `value` 值为准。

例如以上方法可以简写为：

```java
@Repository
@CacheConfig(cacheNames = "mapCache")
public class CacheDao {
    //...
    @Cacheable(key = "targetClass + methodName +#p0")
    public String findByKey(String key) {
        System.out.println("findByKey is called " + key);
        return map.getOrDefault(key, "empty");
    }
    //...
}
```

**查看它的其它属性**

```java
String keyGenerator() default "";  //key的生成器。key/keyGenerator二选一使用
String cacheManager() default "";  //指定缓存管理器
String cacheResolver() default ""; //或者指定获取解析器
```

### 5. 更新@CachePut

`@CachePut`  注解的作用 主要针对方法配置，能够根据方法的请求参数对其结果进行缓存，和 `@Cacheable`  不同的是，它每次都会触发真实方法的调用 。简单来说就是用户更新缓存数据。但需要注意的是该注解的 `value`  和  `key`  必须与要更新的缓存相同，也就是与 `@Cacheable`  相同。

示例：

```java
@Cacheable(key = "targetClass + #p0")
public String findByKey(String key) {
    System.out.println("findByKey is called " + key);
    return map.getOrDefault(key, "empty");
}

@CachePut(key = "targetClass + #p0")
public String updateByKey(String key, String value) {
    map.put(key, value);
    return value;
}
```

> **注：**
>
> 这里的  **@Cacheable(key = "targetClass + #p0")** 与上文中的  **@Cacheable(key = "targetClass + methodName +#p0") **  并不相同。原因就是上文所说的
>
> - 要注意的是该注解的 `value`  和  `key`  必须与要更新的缓存相同，也就是与 `@Cacheable`  相同。
>
> 同时需要注意的是 `@CachePut` 注解的方法返回值不能为 **void**。缓存值的更新与该方法的返回值相关，如果为 **void** 则缓存值会被更新为 **null**

测试方法在 **cn.sjsdfg.cache.CacheTest#testUpdateByKey**

输出结果为：

```java
findByKey is called 1
1
1
-------- mapCache -----------
class cn.sjsdfg.cache.dao.CacheDao1 : 1
-------- mapCache -----------
123
-------- mapCache -----------
class cn.sjsdfg.cache.dao.CacheDao1 : 123
-------- mapCache -----------

```

可以看到 **class cn.sjsdfg.cache.dao.CacheDao1** 的缓存值从 1 变成了 123。

**查看它的其它属性**

```java
String[] cacheNames() default {}; //与value二选一
String keyGenerator() default "";  //key的生成器。key/keyGenerator二选一使用
String cacheManager() default "";  //指定缓存管理器
String cacheResolver() default ""; //或者指定获取解析器
String condition() default ""; //条件符合则缓存
String unless() default ""; //条件符合则不缓存
```

# 参考资料

1. https://www.cnblogs.com/yueshutong/p/9381540.html