

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

**注意：**

- doInRedis 中的 redis 操作不会立刻执行
- 所有 redis 操作会在 connection.closePipeline() 之后一并提交到 redis 并执行，这是 pipeline 方式的优势
- 所有操作的执行结果为 executePipelined() 的返回值

## RedisTemplete 执行 lua 脚本

### Redis 命令行运行 Lua 脚本

假定我们有如下 lua 脚本：

```lua
--获取KEY
local key1 = KEYS[1]
local key2 = KEYS[2]
 
-- 获取ARGV[1],这里对应到应用端是一个List<Map>.
--  注意，这里接收到是的字符串，所以需要用csjon库解码成table类型
local receive_arg_json =  cjson.decode(ARGV[1])
 
--返回的变量
local result = {}
 
--打印日志到reids
--注意，这里的打印日志级别，需要和redis.conf配置文件中的日志设置级别一致才行
redis.log(redis.LOG_DEBUG,key1)
redis.log(redis.LOG_DEBUG,key2)
redis.log(redis.LOG_DEBUG, ARGV[1],#ARGV[1])
 
--获取ARGV内的参数并打印
local expire = receive_arg_json.expire
local times = receive_arg_json.times
redis.log(redis.LOG_DEBUG,tostring(times))
redis.log(redis.LOG_DEBUG,tostring(expire))
 
--往redis设置值
redis.call("set",key1,times)
redis.call("incr",key2)
redis.call("expire",key2,expire)
 
--用一个临时变量来存放json,json是要放入要返回的数组中的
local jsonRedisTemp={}
jsonRedisTemp[key1] = redis.call("get",key1)
jsonRedisTemp[key2] = redis.call("get", key2)
jsonRedisTemp["ttl"] = redis.call("ttl",key2)
redis.log(redis.LOG_DEBUG, cjson.encode(jsonRedisTemp))
 
 
result[1] = cjson.encode(jsonRedisTemp) --springboot redistemplate接收的是List,如果返回的数组内容是json对象,需要将json对象转成字符串,客户端才能接收
result[2] = ARGV[1] --将源参数内容一起返回
redis.log(redis.LOG_DEBUG,cjson.encode(result)) --打印返回的数组结果，这里返回需要以字符返回
 
return result
```

我们可以使用如下命令行查看执行结果：

其基本命令结构如下：

```shell
redis-cli [--ldb] --eval script [numkeys] key [key ...] , arg [arg ...]
```

- **--eval**：告诉redis客户端去加载Lua脚本，后面跟着的就是 lua 脚本的路径
- **--ldb** ：进行命令调试的必要参数
- **numkeys**：指定后续参数有几个key。可省略
- **key [key ...]**：是要操作的键，可以指定多个，在lua脚本中通过`KEYS[1]`, `KEYS[2]`获取
- **arg [arg ...]**，参数，在lua脚本中通过`ARGV[1]`, `ARGV[2]`获取。

> 注意： KEYS和ARGV中间的 ',' 两边的空格，不能省略

针对本例中的 Lua 脚本其对应的命令行如下：

```shell
bin/redis-cli -h localhost -p 7379 -a zcvbnm --ldb --eval script/LimitLoadTimes.lua count rate.limiting:127.0.0.1 , "{\"expire\":\"10000\",\"times\":\"10\"}"
```

其他的一些参数

- **-h** 修改后的ip **-a** 修改后的密码 **-p** 修改后的端口号

结果输出为：

```shell
[root@VM_0_12_centos redis-4.0.8]# bin/redis-cli -h localhost -p 7379 -a zcvbnm --ldb --eval script/LimitLoadTimes.lua count rate.limiting:127.0.0.1 , "{\"expire\":\"10000\",\"times\":\"10\"}"
Lua debugging session started, please use:
quit    -- End the session.
restart -- Restart the script in debug mode again.
help    -- Show Lua script debugging commands.

* Stopped at 1, stop reason = step over
-> 1   local key1 = KEYS[1]
lua debugger> continue

1) "{\"rate.limiting:127.0.0.1\":\"1\",\"count\":\"10\",\"ttl\":10000}"
2) "{\"expire\":\"10000\",\"times\":\"10\"}"
```

### 使用 Java 运行 Lua 脚本

实现代码如下：

```java
package cn.sjsdfg.redis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Joe on 2019/5/8.
 */
@Service
public class LuaScriptService {
    @Autowired
    @Qualifier("customRedisTemplate")
    private RedisTemplate<String, Object> redisTemplate;

    private DefaultRedisScript<List> getRedisScript;

    @PostConstruct
    public void init(){
        getRedisScript = new DefaultRedisScript<List>();
        getRedisScript.setResultType(List.class);
        getRedisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("luascript/LimitLoadTimes.lua")));
    }

    public void redisAddScriptExec(){
        /**
         * List设置lua的KEYS
         */
        List<String> keyList = new ArrayList<>();
        keyList.add("count");
        keyList.add("rate.limiting:127.0.0.1");

        /**
         * 用Mpa设置Lua的ARGV[1]
         */
        Map<String,Object> argvMap = new HashMap<String,Object>();
        argvMap.put("expire", 10000);
        argvMap.put("times", 10);

        /**
         * 调用脚本并执行
         */
        List result = redisTemplate.execute(getRedisScript, keyList, argvMap);
        System.out.println(result);
    }
}
```

测试代码在 **cn.sjsdfg.redis.service.LuaScriptServiceTest#testRedisAddScriptExec**，其输出为：

```java
[{rate.limiting:127.0.0.1=3, count=10, ttl=10000}, {times=10, expire=10000}]
```

与前面直接执行 lua 脚本的输出结果一致。

### 注意

1. Lua脚本可以在redis单机模式、主从模式、Sentinel集群模式下正常使用，但是无法在分片集群模式下使用。（脚本操作的key可能不在同一个分片）
2. **Lua**脚本中尽量避免使用循环操作（可能引发死循环问题），尽量避免长时间运行。
3. redis在执行lua脚本时，默认最长运行时间时5秒，当脚本运行时间超过这一限制后，Redis将开始接受其他命令但不会执行（以确保脚本的原子性，因为此时脚本并没有被终止），而是会返回“BUSY”错误。

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
- [spring-data-keyvalue-examples](https://github.com/spring-projects/spring-data-keyvalue-examples)
- [Spring Data Redis 简介以及项目 Demo，RedisTemplate 和 Serializer 详解 ](https://www.cnblogs.com/edwinchen/p/3816938.html)
- [redisTemplate 常用集合使用说明 (一)](https://357029540.iteye.com/blog/2388706)
- [springboot 之使用 redistemplate 优雅地操作 redis](https://www.cnblogs.com/superfj/p/9232482.html)

## 连接 Redis 工具

- https://github.com/necan/RedisDesktopManager-Windows 提供 RedisManager 的开源编译版本
- https://github.com/onewe/RedisDesktopManager-Mac 软件的 Mac 版本