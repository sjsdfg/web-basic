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

# MongoDB save()方法和insert()方法的区别

 **首先看官方文档怎么说的**

> Updates an existing document or inserts a new document, depending on its document parameter

save方法有更新和插入两种功能，到底是插入还是更新文档取决于save的参数。那么到底是依赖于哪个参数呢？继续看

> If the document does not contain an _id field, then the save() method calls the insert() method. During the operation, the mongo shell will create an ObjectId and assign it to the _id field.

可以看到决定是插入一个文档还是更新，取决于_id参数。如果能根据_id找到一个已经存在的文档，那么就更新。如果没有传入_id参数或者找不到存在的文档，那么就插入一个新文档。

**举一个官方的例子**

不带_id参数

> db.products.save( { item: "book", qty: 40 } )

 **结果**

> { "_id" : ObjectId("50691737d386d8fadbd6b01d"), "item" : "book", "qty" : 40 }

MongoDb客户端驱动会自动为你生成一个默认
ObjectId作为_id。

带_id参数，但是找不到一个已经存在的文档

> db.products.save( { _id: 100, item: "water", qty: 30 } )

**结果**

> { "_id" : 100, "item" : "water", "qty" : 30 }

还是插入一个新文档，但是_id不会自动生成。

带_id参数，但是有存在的文档

> db.products.save( { _id : 100, item : "juice" } )

**结果**

> { "_id" : 100, "item" : "juice" }

更新了文档

## 总结

1. insert: 若新增数据的主键已经存在，则会抛 org.springframework.dao.DuplicateKeyException 异常提示主键重复，不保存当前数据。
2. save: 若新增数据的主键已经存在，则会对当前已经存在的数据进行修改操作。

# MongoDB 分页

## 传统分页思路

假设一页大小为10条。则

```javascript
//page 1
1-10

//page 2
11-20

//page 3
21-30
...

//page n
10*(n-1) +1 - 10*n
```

MongoDB提供了skip()和limit()方法。

- **skip**: 跳过指定数量的数据. 可以用来跳过当前页之前的数据，即跳过pageSize*(n-1)。
- **limit**: 指定从MongoDB中读取的记录条数，可以当做页面大小pageSize。

所以，分页可以这样做：

```javascript
//Page 1
db.users.find().limit (10)
//Page 2
db.users.find().skip(10).limit(10)
//Page 3
db.users.find().skip(20).limit(10)
........
```

**问题**

看起来，分页已经实现了，但是官方文档并不推荐，说会扫描全部文档，然后再返回结果。

> The cursor.skip() method requires the server to scan from the beginning of the input results set before beginning to return results. As the offset increases, cursor.skip() will become slower.

所以，需要一种更快的方式。其实和mysql数量大之后不推荐用limit m,n一样，解决方案是先查出当前页的第一条，然后顺序数pageSize条。MongoDB官方也是这样推荐的。

## 正确的分页办法

我们假设基于_id的条件进行查询比较。事实上，这个比较的基准字段可以是任何你想要的有序的字段，比如时间戳。

```
//Page 1
db.users.find().limit(pageSize);
//Find the id of the last document in this page
last_id = ...
 
//Page 2
users = db.users.find({
  '_id' :{ "$gt" :ObjectId("5b16c194666cd10add402c87")}
}).limit(10)
//Update the last id with the id of the last document in this page
last_id = ...
```

显然，第一页和后面的不同。对于构建分页API, 我们可以要求用户必须传递pageSize, lastId。

- pageSize 页面大小
- lastId 上一页的最后一条记录的id，如果不传，则将强制为第一页

### 降序

`_id`降序，第一页是最大的，下一页的id比上一页的最后的id还小。

```javascript
function printStudents(startValue, nPerPage) {
  let endValue = null;
  db.students.find( { _id: { $lt: startValue } } )
             .sort( { _id: -1 } )
             .limit( nPerPage )
             .forEach( student => {
               print( student.name );
               endValue = student._id;
             } );

  return endValue;
}
```

### 升序

`_id`升序， 下一页的id比上一页的最后一条记录id还大。

```javascript
function printStudents(startValue, nPerPage) {
  let endValue = null;
  db.students.find( { _id: { $gt: startValue } } )
             .sort( { _id: 1 } )
             .limit( nPerPage )
             .forEach( student => {
               print( student.name );
               endValue = student._id;
             } );

  return endValue;
}
```

### 一共多少条

还有一共多少条和多少页的问题。所以，需要先查一共多少条count.

```javascript
db.users.find().count();
```

### ObjectId的有序性问题

先看ObjectId生成规则：

![1557979626493](assets/1557979626493.png)

- 4字节：UNIX时间戳 
- 3字节：表示运行MongoDB的机器 
- 2字节：表示生成此_id的进程 
- 3字节：由一个随机数开始的计数器生成的值 

比如`"_id" : ObjectId("5b1886f8965c44c78540a4fc")`

取id的前4个字节。由于id是16进制的string，4个字节就是32位，对应id前8个字符。即`5b1886f8`, 转换成10进制为`1528334072`. 加上1970，就是当前时间。

事实上，更简单的办法是查看org.mongodb:bson:3.4.3里的ObjectId对象。

```java
public ObjectId(Date date) {
    this(dateToTimestampSeconds(date), MACHINE_IDENTIFIER, PROCESS_IDENTIFIER, NEXT_COUNTER.getAndIncrement(), false);
}

//org.bson.types.ObjectId#dateToTimestampSeconds 
private static int dateToTimestampSeconds(Date time) {
    return (int)(time.getTime() / 1000L);
}

//java.util.Date#getTime
/**
 * Returns the number of milliseconds since January 1, 1970, 00:00:00 GMT
 * represented by this <tt>Date</tt> object.
 *
 * @return  the number of milliseconds since January 1, 1970, 00:00:00 GMT
 *          represented by this date.
 */
public long getTime() {
    return getTimeImpl();
}
```

MongoDB的ObjectId应该是随着时间而增加的，即后插入的id会比之前的大。但考量id的生成规则，最小时间排序区分是秒，同一秒内的排序无法保证。当然，如果是同一台机器的同一个进程生成的对象，是有序的。

如果是分布式机器，不同机器时钟同步和偏移的问题。所以，如果你有个字段可以保证是有序的，那么用这个字段来排序是最好的。`_id`则是最后的备选方案。

## 排序和性能

前面关注于分页的实现原理，但忽略了排序。既然分页，肯定是按照某个顺序进行分页的，所以必须要有排序的。

MongoDB的sort和find组合

```
db.bios.find().sort( { name: 1 } ).limit( 5 )
db.bios.find().limit( 5 ).sort( { name: 1 } )
```

这两个都是等价的，顺序不影响执行顺序。即，都是先find查询符合条件的结果，然后在结果集中排序。

我们条件查询有时候也会按照某字段排序的，比如按照时间排序。查询一组时间序列的数据，我们想要按照时间先后顺序来显示内容，则必须先按照时间字段排序，然后再按照id升序。

```
db.users.find({name: "Ryan"}).sort( { birth: 1, _id: 1 } ).limit( 5 )
```

我们先按照birth升序，然后birth相同的record再按照_id升序，如此可以实现我们的分页功能了。

### 多字段排序

```
db.records.sort({ a:1, b:-1})
```

表示先按照a升序，再按照b降序。即，按照字段a升序，对于a相同的记录，再用b降序，而不是按a排完之后再全部按b排。

示例：

```
db.user.find();

结果：

{ 
    "_id" : ObjectId("5b1886ac965c44c78540a4fb"), 
    "name" : "a", 
    "age" : 1.0, 
    "id" : "1"
}
{ 
    "_id" : ObjectId("5b1886f8965c44c78540a4fc"), 
    "name" : "a", 
    "age" : 2.0, 
    "id" : "2"
}
{ 
    "_id" : ObjectId("5b1886fa965c44c78540a4fd"), 
    "name" : "b", 
    "age" : 1.0, 
    "id" : "3"
}
{ 
    "_id" : ObjectId("5b1886fd965c44c78540a4fe"), 
    "name" : "b", 
    "age" : 2.0, 
    "id" : "4"
}
{ 
    "_id" : ObjectId("5b1886ff965c44c78540a4ff"), 
    "name" : "c", 
    "age" : 10.0, 
    "id" : "5"
}
```

按照名称升序，然后按照age降序

```
db.user.find({}).sort({name: 1, age: -1})

结果：  
{ 
    "_id" : ObjectId("5b1886f8965c44c78540a4fc"), 
    "name" : "a", 
    "age" : 2.0, 
    "id" : "2"
}
{ 
    "_id" : ObjectId("5b1886ac965c44c78540a4fb"), 
    "name" : "a", 
    "age" : 1.0, 
    "id" : "1"
}
{ 
    "_id" : ObjectId("5b1886fd965c44c78540a4fe"), 
    "name" : "b", 
    "age" : 2.0, 
    "id" : "4"
}
{ 
    "_id" : ObjectId("5b1886fa965c44c78540a4fd"), 
    "name" : "b", 
    "age" : 1.0, 
    "id" : "3"
}
{ 
    "_id" : ObjectId("5b1886ff965c44c78540a4ff"), 
    "name" : "c", 
    "age" : 10.0, 
    "id" : "5"
}
```

### 用索引优化排序

到这里必须考虑下性能。

> **$sort and Memory Restrictions**
>
> The sortstagehasalimitof100megabytesofRAM.Bydefault,ifthestageexceedsthislimit,sortstagehasalimitof100megabytesofRAM.Bydefault,ifthestageexceedsthislimit,sort will produce an error. To allow for the handling of large datasets, set the `allowDiskUse` option to true to enable $sort operations to write to temporary files. See the allowDiskUse option in db.collection.aggregate() method and the aggregate command for details.
>
> Changed in version 2.6: The memory limit for $sort changed from 10 percent of RAM to 100 megabytes of RAM.

从2.6开始，sort只排序100M以内的数据，超过将会报错。可以通过设置`allowDiskUse`来允许排序大容量数据。

有索引的排序会比没有索引的排序快，所以官方推荐为需要排序的key建立索引。

### 索引

对于单key排序，建立单独索引

```javascript
db.records.createIndex( { a: 1 } )
```

**索引可以支持同排序和逆序的sort**

索引又分升序(1)和降序(-1)，索引定义的排序方向以及逆转方向可以支持sort。对于上述单key索引a，可以支持`sort({a:1})`升序和`sort({a:-1})`降序。

对于多字段排序

如果想要使用索引。则可以建立复合(compound index)索引为

```javascript
db.records.createIndex( { a: 1, b:-1 } )
```

**复合索引的字段顺序必须和sort一致**

复合多字段索引的顺序要和sort的字段一致才可以走索引。比如索引`{a:1, b:1}`, 可以支持`sort({a:1, b:1})`和逆序`sort({a:-1, b:-1})`， 但是，不支持a，b颠倒。即，不支持`sort({b:1, a:1})`.

**复合索引支持sort同排序和逆序**

索引`{a:1, b:-1}` 可以支持`sort({a:1, b:-1})`, 也可以支持`sort({a:-1, b:1})`

**复合索引可以前缀子集支持sort**

对于多字段复合索引，可以拆分成多个前缀子集。比如`{a:1, b:1, c:1}`相当于

```javascript
{ a: 1 }
{ a: 1, b: 1 }
{ a: 1, b: 1, c: 1 }
```

示例：

| Example                                                  | Index Prefix         |
| -------------------------------------------------------- | -------------------- |
| db.data.find().sort( { a: 1 } )                          | { a: 1 }             |
| db.data.find().sort( { a: -1 } )                         | { a: 1 }             |
| db.data.find().sort( { a: 1, b: 1 } )                    | { a: 1, b: 1 }       |
| db.data.find().sort( { a: -1, b: -1 } )                  | { a: 1, b: 1 }       |
| db.data.find().sort( { a: 1, b: 1, c: 1 } )              | { a: 1, b: 1, c: 1 } |
| db.data.find( { a: { $gt: 4 } } ).sort( { a: 1, b: 1 } ) | { a: 1, b: 1 }       |

**复合索引的非前缀子集可以支持sort，前提是前缀子集的元素要在find的查询条件里是equals**

这个条件比较绕口，复合索引的非前缀子集，只要find和sort的字段要组成索引前缀，并且find里的条件必须是相等。

示例

| Example                                                 | Index Prefix          |
| ------------------------------------------------------- | --------------------- |
| db.data.find( { a: 5 } ).sort( { b: 1, c: 1 } )         | { a: 1 , b: 1, c: 1 } |
| db.data.find( { b: 3, a: 4 } ).sort( { c: 1 } )         | { a: 1, b: 1, c: 1 }  |
| db.data.find( { a: 5, b: { $lt: 3} } ).sort( { b: 1 } ) | { a: 1, b: 1 }        |

find和sort的字段加起来满足前缀子集，find条件中可以使用其他字段进行非equals比较。

对于既不是前缀子集，也不是find相等条件的。索引无效。比如，对于索引`{a:1, b:1, c:1}`。以下两种方式不走索引。

```javascript
db.data.find( { a: { $gt: 2 } } ).sort( { c: 1 } )
db.data.find( { c: 5 } ).sort( { c: 1 } )
```



# 参考资料：

1. [MongoDB分页的Java实现和分页需求的思考](https://www.cnblogs.com/woshimrf/p/mongodb-pagenation-performance.html)
2. [官方分页推荐](https://docs.mongodb.com/manual/reference/method/cursor.skip/)
3. [官方sort文档](https://docs.mongodb.com/manual/reference/operator/aggregation/sort/index.html)
4. [官方使用索引优化sort文档](https://docs.mongodb.com/manual/tutorial/sort-results-with-indexes/)
5. [官方复合索引](https://docs.mongodb.com/manual/core/index-compound/#index-type-compound)
6. [如何正确看待分页的需求](http://www.ovaistariq.net/404/mysql-paginated-displays-how-to-kill-performance-vs-how-to-improve-performance/#.WxiEK4huaUk)
7. http://ian.wang/35.htm
8. https://cnodejs.org/topic/559a0bf493cb46f578f0a601