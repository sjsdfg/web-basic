# Spring-MVC 媒体类型

## REST 内容协商

| 组件名称             | 实现                              | 说明                                                 |
| -------------------- | :-------------------------------- | ---------------------------------------------------- |
| 内容协商管理器       | `ContentNegotiationManager`       | `ContentNegotiationStrategy` 控制策略                |
| 媒体类型             | `MediaType`                       | HTTP 消息媒体类型，如 `text/html`                    |
| 消费媒体类型         | `@RequestMapping#consumes`        | 请求头 `Content-Type`媒体类型映射                    |
| 生产媒体类型         | `@RequestMapping#produces`        | 响应头 `Content-Type`媒体类型映射                    |
| HTTP 消息转换器      | ` HttpMessageConverter`           | HTTP 消息转换器，用于序列化和反序列化 HTTP请求和相应 |
| Web MVC 配置器       | `WebMvcConfigurer`                | 配置 REST 相关的组件                                 |
| 处理方法             | `HandlerMethod`                   | `@RequestMapping`标注的方法                          |
| 处理方法参数解析器   | `HandlerMethodArgumentResolver`   | 用于 HTTP 请求中解析 `HandlerMethod` 参数内容        |
| 处理方法返回值解析器 | `HandlerMethodReturnValueHandler` | 用于 `HandlerMethod` 返回值解析为 HTTP  相应内容     |

  

## 理解请求的媒体类型

经过 `ContentNegotiationManager` 的 `ContentNegotiationStrategy` 解析请求中的媒体类型，比如 `Accept` 请求头

```java
public class ContentNegotiationManager implements org.springframework.web.accept.ContentNegotiationStrategy, org.springframework.web.accept.MediaTypeFileExtensionResolver {
    private final java.util.List<org.springframework.web.accept.ContentNegotiationStrategy> strategies;
    ...
}
```

- 如果成功解析，则返回 `MediaType` 列表
- 否则返回单元素 `*/*` 媒体列表 - `MediaType.ALL`

其中解析的方法在 `org.springframework.web.accept.ContentNegotiationManager#resolveMediaTypes`

## 理解可生成的媒体类型

返回 `@Controller` `HandlerMethod` `@RequestMapping.produces()` 属性所自指定的 `MediaType` 列表：

- 如果 `@RequestMapping.produces()` 存在，返回指定的 `MediaType` 列表
- 否则，返回已经注册的 `HttpMessageConverter` 列表中所支持的 `MediaType` 列表

## 理解 `@RequestMapping#consumes`

用于 `@Controller` `HandlerMethod` 匹配：

- 如果请求头中 `Content-Type`媒体类型兼容 `@RequestMapping#consumes` 属性，则执行对应的  `HandlerMethod`
- 否则 `HandlerMetod` 不会被调用

## 理解 `@RequestMapping#produces`

用于获取可生成的 `MediaType` 列表：

- 如果该列表请求与请求的媒体类型兼容，则执行第一个兼容 `HttpMessageConverter` 的实现，默认 `@RequestMapping#produces` 内容到响应头 `Content-Type`
- 否则，抛出 `HttpMediaTypeNotAcceptableException`，其对应的 **HTTP Error Code：415**

## 自定义 `HandlerMethodArgumentResolver`

### 需求

- 不依赖 `@RequestBody` ， 实现  `Properties` 格式请求内容，解析为  Properties 对象的方法参数
- 复用 PropertiesHttpMessageConverter

### 实现步骤

- 实现 `HandlerMethodArgumentResolver` ->  `PropertiesHandlerMethodArgumentResolver`

- ~~配置  `PropertiesHandlerMethodArgumentResolver` 到  `WebMvcConfigurer#addArgumentResolvers`~~ ：错误实现

  ```java
  @Override
  public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
      // 错误实现： 因为内建 Converter 位置在自定义实现之前
      // 因而会被其他数据类型进行拦截，从而导致无法正确进行 Properties 的属性转换
      //resolvers.add(new PropertiesHandlerMethodArgumentResolver());
  }
  ```

- `RequestMappingHandlerAdapter#setArgumentResolvers` : 正确实现，通过设置 argumentResolvers 讲自定义实现放置在链表头部，以免被 built-in（内建）的 Resolvers 进行拦截

  ```java
  @Autowired
  private RequestMappingHandlerAdapter requestMappingHandlerAdapter;
  
  @PostConstruct
  public void init() {
      List<HandlerMethodArgumentResolver> argumentResolvers = requestMappingHandlerAdapter.getArgumentResolvers();
      List<HandlerMethodArgumentResolver> newResolvers = new ArrayList<>(argumentResolvers.size() + 1);
      newResolvers.add(new PropertiesHandlerMethodArgumentResolver());
      newResolvers.addAll(argumentResolvers);
      requestMappingHandlerAdapter.setArgumentResolvers(newResolvers);
  }
  ```

## 自定义 HandlerMethodReturnValueHandler

### 需求

- 不依赖  `@ResponseBody` ，实现  `Properties` 类型方法返回值，转化为  `Properties` 格式内容响应内容

- 复用 `PropertiesHttpMessageConverter`

### 实现步骤

- 实现 `	-> `PropertiesHandlerMethodReturnValueHandler`
- 配置  `PropertiesHandlerMethodReturnValueHandler` 到  `WebMvcConfigurer#addReturnValueHandlers`
- `RequestMappingHandlerAdapter#setReturnValueHandlers`