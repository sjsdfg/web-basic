

## Redis 的数据类型

### String 字符串

- string 是 redis 最基本的类型，一个 key 对应一个 value。
- string 类型是二进制安全的。意思是 redis 的 string 可以包含任何数据。比如 jpg 图片或者序列化的对象 。
- string 类型是 Redis 最基本的数据类型，一个键最大能存储 512MB。
- [String 类型的操作参考 ](http://www.runoob.com/redis/redis-strings.html)

### 链表

- redis 列表是简单的字符串列表，排序为插入的顺序。列表的最大长度为 2^32-1。
- redis 的列表是使用链表实现的，这意味着，即使列表中有上百万个元素，增加一个元素到列表的头部或尾部的操作都是在常量的时间完成。
- 可以用列表获取最新的内容（像帖子，微博等），用 ltrim 很容易就会获取最新的内容，并移除旧的内容。
- 用列表可以实现生产者消费者模式，生产者调用 lpush 添加项到列表中，消费者调用 rpop 从列表中提取，如果没有元素，则轮询去获取，或者使用 brpop 等待生产者添加项到列表中。
- [List 类型的操作参考 ](http://www.runoob.com/redis/redis-lists.html)

### 集合

- redis 集合是无序的字符串集合，集合中的值是唯一的，无序的。可以对集合执行很多操作，例如，测试元素是否存在，对多个集合执行交集、并集和差集等等。
- 我们通常可以用集合存储一些无关顺序的，表达对象间关系的数据，例如用户的角色，可以用 sismember 很容易就判断用户是否拥有某个角色。
- 在一些用到随机值的场合是非常适合的，可以用 srandmember/spop 获取/弹出一个随机元素。
  同时，使用@EnableCaching 开启声明式缓存支持，这样就可以使用基于注解的缓存技术。注解缓存是一个对缓存使用的抽象，通过在代码中添加下面的一些注解，达到缓存的效果。
- [Set 类型的操作参考 ](http://www.runoob.com/redis/redis-sets.html)

### ZSet 有序集合

- 有序集合由唯一的，不重复的字符串元素组成。有序集合中的每个元素都关联了一个浮点值，称为分数。可以把有序看成 hash 和集合的混合体，分数即为 hash 的 key。
- 有序集合中的元素是按序存储的，不是请求时才排序的。
- [ZSet 类型的操作类型 ](http://www.runoob.com/redis/redis-sorted-sets.html)

### Hash-哈希

- redis 的哈希值是字符串字段和字符串之间的映射，是表示对象的完美数据类型。
- 哈希中的字段数量没有限制，所以可以在你的应用程序以不同的方式来使用哈希。
- [Hash 类型的操作参考 ](http://www.runoob.com/redis/redis-hashes.html)

## 关于 key 的设计

### key 的存活时间：

无论什么时候，只要有可能就利用 key 超时的优势。一个很好的例子就是储存一些诸如临时认证 key 之类的东西。当你去查找一个授权 key 时——以 OAUTH 为例——通常会得到一个超时时间。
这样在设置 key 的时候，设成同样的超时时间，Redis 就会自动为你清除。

### 关系型数据库的 redis

1. 把表名转换为 key 前缀 如, tag:
2. 第 2 段放置用于区分区 key 的字段--对应 mysql 中的主键的列名,如 userid
3. 第 3 段放置主键值,如 2,3,4...., a , b ,c
4. 第 4 段,写要存储的列名

例：user:userid:9:username

## RedisTemplate 常用操作集合

| 方法          | Redis 类型 | 备注                      |
| :------------ | :--------- | ------------------------- |
| opsForValue() | String     | 对 redis 字符串类型数据操作 |
| opsForList()  | List       | 对链表类型的数据操作      |
| opsForHash()  | Hash       | 对 hash 类型的数据操作      |
| opsForSet()   | Set        | 对无序集合类型的数据操作  |
| opsForZSet()  | ZSet       | 对有序集合类型的数据操作  |

## Serializer

目前已经支持的序列化策略：

- **JdkSerializationRedisSerializer**：POJO 对象的存取场景，使用 JDK 本身序列化机制，将 pojo 类通过 ObjectInputStream/ObjectOutputStream 进行序列化操作，最终 redis-server 中将存储字节序列。是目前最常用的序列化策略
- **StringRedisSerializer** ：Key 或者 value 为字符串的场景，根据指定的 charset 对数据的字节序列编码成 string，是 “new String(bytes, charset)” 和 “string.getBytes(charset)” 的直接封装。是最轻量级和高效的策略。
- **JacksonJsonRedisSerializer**：jackson-json 工具提供了 javabean 与 json 之间的转换能力，可以将 pojo 实例序列化成 json 格式存储在 redis 中，也可以将 json 格式的数据转换成 pojo 实例。因为 jackson 工具在序列化和反序列化时，需要明确指定 Class 类型，因此此策略封装起来稍微复杂。【需要 jackson-mapper-asl 工具支持】
- **OxmSerializer** ：提供了将 javabean 与 xml 之间的转换能力，目前可用的三方支持包括 jaxb，apache-xmlbeans；redis 存储的数据将是 xml 工具。不过使用此策略，编程将会有些难度，而且效率最低；不建议使用。【需要 spring-oxm 模块的支持】

其中 **JdkSerializationRedisSerializer** 和 **StringRedisSerializer** 是最基础的序列化策略，其中 “JacksonJsonRedisSerializer” 与 “OxmSerializer” 都是基于 String 存储，因此它们是较为“高级”的序列化 (最终还是使用 string 解析以及构建 java 对象)。

> **如果你的数据需要被第三方工具解析，那么数据应该使用 StringRedisSerializer 而不是 JdkSerializationRedisSerializer。**

## Redis Pipline

通过 RedisTemplete 实现 pipline 可以参考如下代码：

```java
public List<Object> queryAll() {
    return redisTemplate.executePipelined((RedisConnection redisConnection) -> {
        RedisSerializer<String> stringSerializer = redisTemplate.getStringSerializer();
        Set<String> keys = redisTemplate.keys("*");
        if (Objects.nonNull(keys)) {
            for (String key : keys) {
                redisConnection.get(stringSerializer.serialize(key));
            }
        }
        return null;
    });
}
```

需要注意的是 `redisTemplate.executePipelined()` 里面的方法返回值必须为 null.

原因是该方法的源码如下：

```java
public List<Object> executePipelined(final RedisCallback<?> action) {
    return executePipelined(action, valueSerializer);
}

public List<Object> executePipelined(final RedisCallback<?> action, final RedisSerializer<?> resultSerializer) {
    return execute(new RedisCallback<List<Object>>() {
        public List<Object> doInRedis(RedisConnection connection) throws DataAccessException {
            connection.openPipeline();
            boolean pipelinedClosed = false;
            try {
                Object result = action.doInRedis(connection);
                if (result != null) {
                    throw new InvalidDataAccessApiUsageException(
                            "Callback cannot return a non-null value as it gets overwritten by the pipeline");
                }
                List<Object> closePipeline = connection.closePipeline();
                pipelinedClosed = true;
                return deserializeMixedResults(closePipeline, resultSerializer, resultSerializer, resultSerializer);
            } finally {
                if (!pipelinedClosed) {
                    connection.closePipeline();
                }
            }
        }
    });
}
```

在代码段中有如下的判断：

```java
Object result = action.doInRedis(connection);
if (result != null) {
    throw new InvalidDataAccessApiUsageException(
        "Callback cannot return a non-null value as it gets overwritten by the pipeline");
}
```

因此如果所传入的方法如果不为空，则会抛出异常，导致程序运行失败。

### 注意

- doInRedis 中的 redis 操作不会立刻执行
- 所有 redis 操作会在 connection.closePipeline() 之后一并提交到 redis 并执行，这是 pipeline 方式的优势
- 所有操作的执行结果为 executePipelined() 的返回值

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