# Spring-MVC 异步请求

- [异步内容文档](https://docs.spring.io/spring/docs/5.2.0.BUILD-SNAPSHOT/spring-framework-reference/web.html#mvc-ann-async)

## 返回 Callable

1. 控制返回 Callable 之后
2. SpringMVC 异步处理，将 Callable 提交到 TaskExecutor 中进行任务执行，使用隔离线程进行执行
3. DispatchServlet 和所有的 Filter 退出 Web 容器的线程，但是 response 保持打开状态
4. Callable 返回结果，SpringMVC 将请求重新派发到 Servlet 容器，恢复之前的处理状态
5. DispatchServlet  根据 Callable 返回的结果，继续进行视图渲染流程等（从收请求 -> 视图渲染）



## DeferredResult

Once the asynchronous request processing feature is [enabled](https://docs.spring.io/spring/docs/5.2.0.BUILD-SNAPSHOT/spring-framework-reference/web.html#mvc-ann-async-configuration) in the Servlet container, controller methods can wrap any supported controller method return value with `DeferredResult`, as the following example shows:

```java
@GetMapping("/quotes")
@ResponseBody
public DeferredResult<String> quotes() {
    DeferredResult<String> deferredResult = new DeferredResult<String>();
    // Save the deferredResult somewhere..
    return deferredResult;
}

// From some other thread...
deferredResult.setResult(data);
```

The controller can produce the return value asynchronously, from a different thread — for example, in response to an external event (JMS message), a scheduled task, or other event.



## CompletionStage

> @since java 1.8 

- 如果不是使用高于 1.8 的 jdk 版本则无法使用该对象包装返回结果实现异步操作
- 所使用的异步线程池为 `ForkJoinPool.commonPool`

## 异步拦截器

1. 原生 API 的 `AysncListener`
2. SpringMVC 所带的 `AysncHandlerInterceptor`