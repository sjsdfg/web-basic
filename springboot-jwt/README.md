# Spring Boot - JWT

## Bearer 认证

HTTP 提供了一套标准的身份验证框架：服务器可以用来针对客户端的请求发送质询 (challenge)，客户端根据质询提供身份验证凭证。质询与应答的工作流程如下：服务器端向客户端返回 401（Unauthorized，未授权）状态码，并在 WWW-Authenticate 头中添加如何进行验证的信息，其中至少包含有一种质询方式。然后客户端可以在请求中添加 Authorization 头进行验证，其 Value 为身份验证的凭证信息。

![347047-20171101002006466-773250344](img\347047-20171101002006466-773250344.png)

在 HTTP 标准验证方案中，我们比较熟悉的是"Basic"和"Digest"，前者将用户名密码使用 BASE64 编码后作为验证凭证，后者是 Basic 的升级版，更加安全，因为 Basic 是明文传输密码信息，而 Digest 是加密后传输。

**Bearer**验证也属于 HTTP 协议标准验证，它随着 OAuth 协议而开始流行，详细定义见： [RFC 6570](https://tools.ietf.org/html/rfc6750#section-1.2)。

> A security token with the property that any party in possession of the token (a "bearer") can use the token in any way that any other party in possession of it can. Using a bearer token does not require a bearer to prove possession of cryptographic key material (proof-of-possession).

Bearer 验证中的凭证称为`BEARER_TOKEN`，或者是`access_token`，它的颁发和验证完全由我们自己的应用程序来控制，而不依赖于系统和 Web 服务器，Bearer 验证的标准请求方式如下：

> Authorization: Bearer [BEARER_TOKEN] 

那么使用 Bearer 验证有什么好处呢？

- CORS: cookies + CORS 并不能跨不同的域名。而 Bearer 验证在任何域名下都可以使用 HTTP header 头部来传输用户信息。
- 对移动端友好: 当你在一个原生平台 (iOS, Android, WindowsPhone 等) 时，使用 Cookie 验证并不是一个好主意，因为你得和 Cookie 容器打交道，而使用 Bearer 验证则简单的多。
- CSRF: 因为 Bearer 验证不再依赖于 cookies, 也就避免了跨站请求攻击。
- 标准：在 Cookie 认证中，用户未登录时，返回一个`302`到登录页面，这在非浏览器情况下很难处理，而 Bearer 验证则返回的是标准的`401 challenge`。

## JWT(JSON WEB TOKEN)

上面介绍的 Bearer 认证，其核心便是**BEARER_TOKEN**，而最流行的 Token 编码方式便是：JSON WEB TOKEN。

> Json web token (JWT), 是为了在网络应用环境间传递声明而执行的一种基于 JSON 的开放标准（[RFC 7519](https://tools.ietf.org/html/rfc7519)）。该 token 被设计为紧凑且安全的，特别适用于分布式站点的单点登录（SSO）场景。JWT 的声明一般被用来在身份提供者和服务提供者间传递被认证的用户身份信息，以便于从资源服务器获取资源，也可以增加一些额外的其它业务逻辑所必须的声明信息，该 token 也可直接被用于认证，也可被加密。

### 头部 (Header)

Header 一般由两个部分组成：

- alg
- typ

`alg`是是所使用的 hash 算法，如：HMAC SHA256 或 RSA，`typ`是 Token 的类型，在这里就是：**JWT**。

```json
{
  "alg": "HS256",
  "typ": "JWT"
}
```

然后使用 Base64Url 编码成第一部分：

```
eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.<second part>.<third part>
```

### 载荷 (Payload)

这一部分是 JWT 主要的信息存储部分，其中包含了许多种的声明（claims）。

Claims 的实体一般包含用户和一些元数据，这些 claims 分成三种类型：

- **reserved claims**：预定义的 一些声明，并不是强制的但是推荐，它们包括 iss (issuer), exp (expiration time), sub (subject),aud(audience) 等（这里都使用三个字母的原因是保证 JWT 的紧凑）。
- **public claims**: 公有声明，这个部分可以随便定义，但是要注意和 IANA JSON Web Token 冲突。
- **private claims**: 私有声明，这个部分是共享被认定信息中自定义部分。

```json
{
    "iss": "Issuer —— 用于说明该 JWT 是由谁签发的", 
    "sub": "Subject —— 用于说明该 JWT 面向的对象", 
    "aud": "Audience —— 用于说明该 JWT 发送给的用户", 
    "exp": "Expiration Time —— 数字类型，说明该 JWT 过期的时间", 
    "nbf": "Not Before —— 数字类型，说明在该时间之前 JWT 不能被接受与处理", 
    "iat": "Issued At —— 数字类型，说明该 JWT 何时被签发", 
    "jti": "JWT ID —— 说明标明 JWT 的唯一 ID", 
    "user-definde1": "自定义属性举例", 
    "user-definde2": "自定义属性举例"
}
```

一个简单的 Payload 可以是这样子的：

```json
{
  "sub": "1234567890",
  "name": "John Doe",
  "admin": true
}
```

这部分同样使用 Base64Url 编码成第二部分：

```
eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWV9.<third part>
```

### 签名 (Signature)

Signature 是用来验证发送者的 JWT 的同时也能确保在期间不被篡改。

Signature是由Header和Payload组合而成，将Header和Claim这两个Json分别使用Base64方式进行编码，生成字符串Header和Payload，然后将Header和Payload以Header.Payload的格式组合在一起形成一个字符串，然后使用上面定义好的加密算法和一个密匙（这个密匙存放在服务器上，用于进行验证）对这个字符串进行加密，形成一个新的字符串，这个字符串就是Signature。

在创建该部分时候你应该已经有了编码后的 Header 和 Payload，然后使用保存在服务端的秘钥对其签名，一个完整的 JWT 如下：

```
eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWV9.TJVA95OrM7E2cBab30RMHrHDcEfxjoYZgeFONFh7HgQ
```

因此使用 JWT 具有如下好处：

- **通用**：因为 json 的通用性，所以 JWT 是可以进行跨语言支持的，像 JAVA,JavaScript,NodeJS,PHP 等很多语言都可以使用。
- **紧凑**：JWT 的构成非常简单，字节占用很小，可以通过 GET、POST 等放在 HTTP 的 header 中，非常便于传输。
- **扩展**：JWT 是自我包涵的，包含了必要的所有信息，不需要在服务端保存会话信息, 非常易于应用的扩展。

关于更多 JWT 的介绍，网上非常多，这里就不再多做介绍。下面，演示一下 ASP.NET Core 中 JwtBearer 认证的使用方式。

### 总结

![20180310163806888](img\20180310163806888.png)



## JWT 用户认证

服务器在生成一个JWT之后会将这个JWT会以 `Authorization : Bearer JWT` 键值对的形式存放在cookies里面发送到客户端机器，在客户端再次访问收到JWT保护的资源URL链接的时候，服务器会获取到请求头中存放的JWT信息，首先将Header进行反编码获取到加密的算法，在通过存放在服务器上的密匙对 `Header.Payload` 这个字符串进行加密，比对JWT中的Signature和实际加密出来的结果是否一致，如果一致那么说明该JWT是合法有效的，认证成功，否则认证失败。

**流程图如下：**

![20180310125157455](img\20180310125157455.png)

> **注：** 流程图肯定是有问题的。 JWT 信息应该是放在 Request Header 里面的。 跟 Cookies 应该没有任何关系

## 示例

```shell
curl -X POST \
  http://localhost:8080/register \
  -H 'Content-Type: application/json' \
  -H 'Postman-Token: 0641c7fa-8946-49dd-83d7-0e194c5afaa7' \
  -H 'cache-control: no-cache' \
  -d '{
	username: "test",
	password: "test"
}'
```

```shell
curl -X GET \
  http://localhost:8080/secure/users/user \
  -H 'Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0Iiwicm9sZXMiOiJtZW1iZXIiLCJpYXQiOjE1NTY0NDM2NTl9.-TQe5EozZQKKYEo9-NDYivoLDzm6o5BlGOZij5oDYY0' \
  -H 'Postman-Token: 2f6c3455-1727-4799-9065-6de0f4afcefd' \
  -H 'cache-control: no-cache'
```

# 参考资料

1. [Spring Boot 整合 JWT 实现用户认证 ](https://blog.csdn.net/ltl112358/article/details/79507148)
2. [JwtBearer 认证 ](https://www.cnblogs.com/Leo_wl/p/7792046.html#_label4)