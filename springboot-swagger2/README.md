# SpringBoot-Swagger2

## 官方文档

- [Swgger2](https://swagger.io/)
- [springfox-swagger](https://springfox.github.io/springfox/docs/current/)

## springfox-swagger2 与 swagger 的区别

**原文如下：** [Answers to common questions and problems](http://springfox.github.io/springfox/docs/current/#answers-to-common-questions-and-problems)

**Q. What is the relationship between swagger-ui and springfox-swagger-ui?**

1. It can be a little confusing:
   - Swagger Spec is a specification.
   - Swagger Api - an implementation of that specification that supports jax-rs, restlet, jersey etc.
   - Springfox libraries in general - another implementation of the specification focused on the spring based ecosystem.
   - Swagger.js and Swagger-ui - are client libraries in javascript that can consume swagger specification.
   - springfox-swagger-ui - the one that you’re referring to, is just packaging swagger-ui in a convenient way so that spring services can serve it up.

翻译之后的内容为：

1. Swagger Spec 是一个规范。
2. Swagger Api 是 Swagger Spec 规范 的一个实现，它支持 jax-rs, restlet, jersey 等等。
3. Springfox libraries 是 Swagger Spec 规范 的另一个实现，专注于 spring 生态系统。
4. Swagger.js and Swagger-ui 是 javascript 的客户端库，可以使用 Swagger Spec规范 。
5. springfox-swagger-ui 仅仅是以一种方便的方式封装了 swagger-ui ，使得 Spring 服务可以提供服务。

### 小结

1. Swagger 是一种规范。
2. springfox-swagger 是基于 Spring 生态系统的该规范的实现。
3. springfox-swagger-ui 是对 swagger-ui 的封装，使得其可以使用 Spring 的服务。

## Swagger 整合

### 依赖

```xml
<dependency>
	<groupId>io.springfox</groupId>
	<artifactId>springfox-swagger2</artifactId>
	<version>2.9.2</version>
</dependency>

<dependency>
	<groupId>io.springfox</groupId>
	<artifactId>springfox-swagger-ui</artifactId>
	<version>2.9.2</version>
</dependency>
```

### 常用注解

swagger通过注解表明该接口会生成文档，包括接口名、请求方法、参数、返回信息的等等。

- @Api：修饰整个类，描述Controller的作用
- @ApiOperation：描述一个类的一个方法，或者说一个接口
- @ApiParam：单个参数描述
- @ApiModel：用对象来接收参数
- @ApiProperty：用对象接收参数时，描述对象的一个字段
- @ApiResponse：HTTP响应其中1个描述
- @ApiResponses：HTTP响应整体描述
- @ApiIgnore：使用该注解忽略这个API
- @ApiError ：发生错误返回的信息
- @ApiImplicitParam：一个请求参数
- @ApiImplicitParams：多个请求参数