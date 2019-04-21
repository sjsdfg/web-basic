# Spring Boot Servlet Web

## Spring Boot 嵌入式 Servlet 容器限制

| Servlet 特性                  | 兼容性   | 解决方案                            |
| ----------------------------- | -------- | ----------------------------------- |
| `web.xml`                     | 不支持   | `RegistrationBean` 或  `@Bean` 注册 |
| `ServletContainerInitializer` | 不支持   | `ServletContextInitializer`         |
| `@WebServlet` 等              | 有限支持 | 依赖  `@ServletComponentScan`       |

### 参考资料一

[87.2 Convert an Existing Application to Spring Boot](https://docs.spring.io/spring-boot/docs/2.0.x/reference/htmlsingle/#howto-convert-an-existing-application-to-spring-boot)

> you may need to add some configuration to your `Application` context, by replacing those elements from the `web.xml`, as follows:
>
> - A `@Bean` of type `Servlet` or `ServletRegistrationBean` installs that bean in the container as if it were a `<servlet/>` and `<servlet-mapping/>` in `web.xml`.
> - A `@Bean` of type `Filter` or `FilterRegistrationBean` behaves similarly (as a `<filter/>` and `<filter-mapping/>`).
> - An `ApplicationContext` in an XML file can be added through an `@ImportResource` in your `Application`. Alternatively, simple cases where annotation configuration is heavily used already can be recreated in a few lines as `@Bean` definitions.

###  参考资料二

[28.4.2 Servlet Context Initialization](https://docs.spring.io/spring-boot/docs/2.0.x/reference/htmlsingle/#boot-features-embedded-container-context-initializer)

> Embedded servlet containers do not directly execute the Servlet 3.0+ `javax.servlet.ServletContainerInitializer` interface or Spring’s`org.springframework.web.WebApplicationInitializer` interface. This is an intentional design decision intended to reduce the risk that third party libraries designed to run inside a war may break Spring Boot applications.
>
> If you need to perform servlet context initialization in a Spring Boot application, you should register a bean that implements the`org.springframework.boot.web.servlet.ServletContextInitializer` interface. The single `onStartup` method provides access to the `ServletContext` and, if necessary, can easily be used as an adapter to an existing `WebApplicationInitializer`.

### 参考资料三

[Scanning for Servlets, Filters, and listeners](https://docs.spring.io/spring-boot/docs/2.0.x/reference/htmlsingle/#boot-features-embedded-container-servlets-filters-listeners-scanning)

> When using an embedded container, automatic registration of classes annotated
> with  @WebServlet ,  @WebFilter , and  @WebListener  can be enabled by using  @ServletComponentScan .