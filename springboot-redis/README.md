





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

### 参考资料

- [Spring Data Redis](https://docs.spring.io/spring-data/data-redis/docs/current/reference/html/#new-in-2.1.0) 可查询 feature 演进和版本对应关系
- [**spring-data-keyvalue-examples**](https://github.com/spring-projects/spring-data-keyvalue-examples)