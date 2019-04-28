# SpringBoot-MongoDB

# 依赖

```xml
<dependencies>
    <dependency> 
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-mongodb</artifactId>
    </dependency> 
</dependencies>
```

# 数据源 url 设置

MongoDB 数据源形如：

```shell
mongodb://[user:pwd@]ip1:port1
```

其中的 user 和 pwd 是可选项。如果需要构造多个数据源，则配置如下：

```shell
mongodb://[user:pwd@]ip1:port1,ip2:port2/database
```

## Properties 配置

```properties
spring.data.mongodb.uri=mongodb://name:pass@localhost:27017/test
```

多数据源配置不能依赖 SpringBoot-data 的自动注入，需要自己实现 Bean 的注册：

参考资料：[Spring Boot 中 MongoDB 的使用](https://www.cnblogs.com/ityouknow/p/6828919.html)

## YML 配置

```yaml
spring:
  data:
    mongodb:
      uri: mongodb://192.168.99.100:27017/Test
```

# 常用注解

### @Document

```java
@Document(collection = "users")
```

collection 参数指定数据库表中对应的数据库表名。如果之前不存在该数据库，则会自动创建。

### @CompoundIndexes

用于增加索引，实际索引的定义是通过 **@CompoundIndex** 来定义的：

```java
/**
 * Mark a class to use compound indexes.
 *
 * @author Jon Brisbin
 * @author Oliver Gierke
 * @author Philipp Schneider
 * @author Johno Crawford
 * @author Christoph Strobl
 */
@Target({ ElementType.TYPE })
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface CompoundIndex
```

其中常用的属性值为：

- name()：the name of the index to be created。注解定义的名称
- def()：The actual index definition in JSON format. The keys of the JSON document are the fields to be indexed,the values define the index direction (1 for ascending, -1 for descending).
- unique()：唯一索引

### @Id

MongoDB默认会为每个document生成一个 _id 属性，作为默认主键，且默认值为ObjectId,可以更改 _id 的值(可为空字符串)，但每个document必须拥有 _id 属性。

当然，也可以自己设置@Id主键，不过官方建议使用MongoDB自动生成。

### @Indexed

声明该字段需要加索引，加索引后以该字段为条件检索将大大提高速度。 
唯一索引的话是@Indexed(unique = true)。 
也可以对数组进行索引，如果被索引的列是数组时，mongodb会索引这个数组中的每一个元素。

```java
@Indexed
private String uid;
```

### @Transient

被该注解标注的，将不会被录入到数据库中。只作为普通的 Java Bean 属性。

```java
@Transient
private String address;
```

### @Field

代表一个字段，可以不加，不加的话默认以参数名为列名。

```java
@Field("firstName")
private String name;
```