# Spring-MVC 注解实现

1. web 容器在启动的时候，会扫描每个 jar 包下的 META-INF/services/javax.servlet.ServletContainerInitializer
2. 加载这个文件指定的类 SpringServletContainerInitializer
3. spring 的应用一启动会加载感兴趣的 WebApplicationInitializer 接口的下的所有组件；
4. 并且为 WebApplicationInitializer 组件创建对象（组件不是接口，不是抽象类）
   1. AbstractContextLoaderInitializer：创建根容器；createRootApplicationContext()；
   2. AbstractDispatcherServletInitializer：
      1. 创建一个 web 的 ioc 容器；createServletApplicationContext();
      2. 创建了 DispatcherServlet；createDispatcherServlet()；
      3. 将创建的 DispatcherServlet 添加到 ServletContext 中；getServletMappings();
   3. AbstractAnnotationConfigDispatcherServletInitializer：注解方式配置的 DispatcherServlet 初始化器
      1. 创建根容器：createRootApplicationContext() -> getRootConfigClasses();传入一个配置类
      2. 创建 web 的 ioc 容器： createServletApplicationContext(); -> 获取配置类；getServletConfigClasses();



**总结：** 以注解方式来启动 SpringMVC；继承 AbstractAnnotationConfigDispatcherServletInitializer；实现抽象方法指定 DispatcherServlet 的配置信息；



## 容器配置

通过 AppConfig 和 RootConfig 来形成父子容器的注册

- RootConfig ： 扫描所有非 Controller 的组件

  ```java
  package cn.sjsdfg.config;
  
  import org.springframework.context.annotation.ComponentScan;
  import org.springframework.context.annotation.ComponentScan.Filter;
  import org.springframework.context.annotation.FilterType;
  import org.springframework.stereotype.Controller;
  
  /**
   * Created by Joe on 2019/3/26.
   * Spring 父容器不扫描 Controller
   */
  @ComponentScan(value = "cn.sjsdfg", excludeFilters = {
          @Filter(type= FilterType.ANNOTATION, classes = {Controller.class})
  })
  public class RootConfig {
  }
  ```

  

- AppConfig ：只扫描 Controller 的组件

  ```java
  package cn.sjsdfg.config;
  
  import org.springframework.context.annotation.ComponentScan;
  import org.springframework.context.annotation.ComponentScan.Filter;
  import org.springframework.context.annotation.FilterType;
  import org.springframework.stereotype.Controller;
  
  /**
   * Created by Joe on 2019/3/26.
   * Spring MVC 子容器只扫描 Controller
   * useDefaultFilters = false 禁用默认的过滤规则（默认扫描所有）
   */
  @ComponentScan(value = "cn.sjsdfg", includeFilters = {
          @Filter(type= FilterType.ANNOTATION, classes = Controller.class)
  }, useDefaultFilters = false)
  public class AppConfig {
  }
  ```

  

## MVC Config

- [Spring 官方文档索引](https://spring.io/docs/reference)

定制 Spring MVC

1. @EnableWebMvc ：开启 Spring MVC 定制配置功能 `<mvc:annotation-driven />`

2. 配置组件（视图解析器、视图映射、静态资源映射、拦截器）：实现 `implements WebMvcConfigurer` 接口

   1. 视图解析器

      ```java
      @Override
      public void configureViewResolvers(ViewResolverRegistry registry) {
          // 视图解析器
          // 默认所有的页面都从 WEB-INF/xxx.jsp
          //        registry.jsp();
          // 也可以
          registry.jsp("/WEB-INF/views/", ".jsp");
      }
      ```

      

   2. 静态资源映射

      ```java
      @Override
      public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
          // 开启默认拦截器，不拦截静态资源
          // <mvc:default-servlet-handler />
          configurer.enable();
      }
      ```

   3. 拦截器

      ```java
      @Override
      public void addInterceptors(InterceptorRegistry registry) {
          // 注册拦截器
          registry.addInterceptor(new MyFirstInterceptor()).addPathPatterns("/**");
      }
      ```
   4. others  mvc config：https://docs.spring.io/spring/docs/5.2.0.BUILD-SNAPSHOT/spring-framework-reference/web.html#mvc-config