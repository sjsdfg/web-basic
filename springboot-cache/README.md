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

# 参考资料
1. https://www.cnblogs.com/yueshutong/p/9381540.html