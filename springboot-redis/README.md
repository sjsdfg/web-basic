

## RedisTemplate 常用操作集合

| 方法          | Redis 类型 |
| :------------ | :--------- |
| opsForValue() | String     |
| opsForList()  | List       |
| opsForHash()  | Hash       |
| opsForSet()   | Set        |
| opsForZSet()  | ZSet       |

## Serializer

目前已经支持的序列化策略：

- JdkSerializationRedisSerializer
- StringRedisSerializer
- JacksonJsonRedisSerializer
- OxmSerializer

其中 JdkSerializationRedisSerializer 和 StringRedisSerializer 是最基础的序列化策略，其中 “JacksonJsonRedisSerializer” 与 “OxmSerializer” 都是基于 String 存储，因此它们是较为“高级”的序列化 (最终还是使用 string 解析以及构建 java 对象)。

```java

```



## spring-data-redis 和 jedis版本对应收集总结

如果不使用对饮版本的 Jedis，在项目构建的时候必定会出现 **java.lang.NoClassFoundException**。

Jedis 代码重构变革很大

| spring-data-redis版本 | jedis版本    | 备注 |
| --------------------- | ------------ | ---- |
| 1.5.2.RELEASE         | 2.7.3        |      |
| 1.6.0.RELEASE         | 2.7.2  2.7.3 |      |
| 1.6.2.RELEASE         | 2.8.0        |      |
| 1.8.1.RELEASE         | 2.9.0        |      |
| 1.8.4.RELEASE         | 2.9.0        |      |
| 2.1.x.RELEASE         | 2.9.0        |      |

## 参考资料

- [Spring Data Redis](https://docs.spring.io/spring-data/data-redis/docs/current/reference/html/#new-in-2.1.0) 可查询 feature 演进和版本对应关系
- [**spring-data-keyvalue-examples**](https://github.com/spring-projects/spring-data-keyvalue-examples)
- [Spring Data Redis简介以及项目Demo，RedisTemplate和 Serializer详解](https://www.cnblogs.com/edwinchen/p/3816938.html)
- [redisTemplate常用集合使用说明(一)](https://357029540.iteye.com/blog/2388706)

## 连接 Redis 工具

- https://github.com/necan/RedisDesktopManager-Windows 提供 RedisManager 的开源编译版本
- https://github.com/onewe/RedisDesktopManager-Mac 软件的 Mac 版本