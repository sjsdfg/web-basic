# Servlet 3.0

注册请求服务主要依靠如下三个注解：

- @WebServlet：注册 Servlet
- @WebFilter：注册过滤器
- @WebListener： 注册监听器

当 WebServlet 和 WebFilter 需要初始化参数的时候，则需要使用 `@WebInitParam` 注解

## Shared libraries / runtimes pluggability

在这个规范中有个十分重要的功能是 `Shared libraries / runtimes pluggability（共享库/运行时插件能力）`

1. Servlet 容器启动时会扫描当前应用中每一个 jar 包中 `ServletContainerInitializer` 的实现
2. 提供的 `ServletContainerInitializer` 实现类必须绑定在 `META-INF/services` 目录下 
    - 在这个目录下会有一个文件 `javax.servlet.ServletContainerInitializer`，其中文件的内容就是 `ServletContainerInitializer` 实现的全类名。

**总结：** 容器在启动的时候，会扫描应用每一个 jar 包中 `META-INF/services/javax.servlet.ServletContainerInitializer` 指定的实现类，启动并实现这个类。

Spring Boot 的启动也是基于以上的能力：
1. 服务器启动（web 应用启动）会创建当前 web 应用里面每一个 jar 包里面 ServletContainerInitializer 实例：
2. org\springframework\spring-web\4.3.14.RELEASE\spring-web-4.3.14.RELEASE.jar!\META-INF\services\javax.servlet.ServletContainerInitializer： Spring 的 web 模块里面有这个文件：org.springframework.web.SpringServletContainerInitializer
3. SpringServletContainerInitializer 将@HandlesTypes(WebApplicationInitializer.class) 标注的所有这个类型的类都传入到 onStartup 方法的 Set<Class<?>>；为这些 WebApplicationInitializer 类型的类创建实例；
4. 每一个 WebApplicationInitializer 都调用自己的 onStartup；
5. 相当于我们的 SpringBootServletInitializer 的类会被创建对象，并执行 onStartup 方法
6. SpringBootServletInitializer 实例执行 onStartup 的时候会 createRootApplicationContext；创建容器

## ServletContext 注册组件

全部实现在 `cn.sjsdfg.servlet.MyServletContainerInitializer.onStartup` 中
使用 ServletContext 注册常见的三大组件：
1. Servlet
```java
// 注册组件
ServletRegistration.Dynamic userServlet = servletContext.addServlet("userServlet", new UserServlet());
// 配置映射信息
userServlet.addMapping("/user");
```
2. Filter
```java
 // 注册 Filter
FilterRegistration.Dynamic userFilter = servletContext.addFilter("userFilter", new UserFilter());
// 配置 Filter 的映射信息
userFilter.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, "/*");
```
3. Listener
```java
// 注册 Listener
servletContext.addListener(UserListener.class);
```