

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

- **JdkSerializationRedisSerializer**：POJO 对象的存取场景，使用 JDK 本身序列化机制，将 pojo 类通过 ObjectInputStream/ObjectOutputStream 进行序列化操作，最终 redis-server 中将存储字节序列。是目前最常用的序列化策略
- **StringRedisSerializer** ：Key 或者 value 为字符串的场景，根据指定的 charset 对数据的字节序列编码成 string，是 “new String(bytes, charset)” 和 “string.getBytes(charset)” 的直接封装。是最轻量级和高效的策略。
- **JacksonJsonRedisSerializer**：jackson-json 工具提供了 javabean 与 json 之间的转换能力，可以将 pojo 实例序列化成 json 格式存储在 redis 中，也可以将 json 格式的数据转换成 pojo 实例。因为 jackson 工具在序列化和反序列化时，需要明确指定 Class 类型，因此此策略封装起来稍微复杂。【需要 jackson-mapper-asl 工具支持】
- **OxmSerializer** ：提供了将 javabean 与 xml 之间的转换能力，目前可用的三方支持包括 jaxb，apache-xmlbeans；redis 存储的数据将是 xml 工具。不过使用此策略，编程将会有些难度，而且效率最低；不建议使用。【需要 spring-oxm 模块的支持】

其中 **JdkSerializationRedisSerializer** 和 **StringRedisSerializer** 是最基础的序列化策略，其中 “JacksonJsonRedisSerializer” 与 “OxmSerializer” 都是基于 String 存储，因此它们是较为“高级”的序列化 (最终还是使用 string 解析以及构建 java 对象)。

> **如果你的数据需要被第三方工具解析，那么数据应该使用 StringRedisSerializer 而不是 JdkSerializationRedisSerializer。**

## spring-data-redis 和 jedis 版本对应收集总结

如果不使用对饮版本的 Jedis，在项目构建的时候必定会出现 **java.lang.NoClassFoundException**。

Jedis 代码重构变革很大

| spring-data-redis 版本 | jedis 版本    | 备注 |
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
- [Spring Data Redis 简介以及项目 Demo，RedisTemplate 和 Serializer 详解 ](https://www.cnblogs.com/edwinchen/p/3816938.html)
- [redisTemplate 常用集合使用说明 (一)](https://357029540.iteye.com/blog/2388706)
- [[springboot 之使用 redistemplate 优雅地操作 redis](https://www.cnblogs.com/superfj/p/9232482.html)](http://www.cnblogs.com/superfj/p/9232482.html)

## 连接 Redis 工具

- https://github.com/necan/RedisDesktopManager-Windows 提供 RedisManager 的开源编译版本
- https://github.com/onewe/RedisDesktopManager-Mac 软件的 Mac 版本