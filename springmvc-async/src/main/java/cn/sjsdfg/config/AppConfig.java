package cn.sjsdfg.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.FilterType;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.config.annotation.*;

/**
 * Created by Joe on 2019/3/26.
 * Spring MVC 子容器只扫描 Controller
 * useDefaultFilters = false 禁用默认的过滤规则（默认扫描所有）
 */
@ComponentScan(value = "cn.sjsdfg", includeFilters = {
        @Filter(type= FilterType.ANNOTATION, classes = Controller.class)
}, useDefaultFilters = false)
@EnableWebMvc
public class AppConfig implements WebMvcConfigurer {
    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
        // 视图解析器
        // 默认所有的页面都从 WEB-INF/xxx.jsp
//        registry.jsp();
        // 也可以
        registry.jsp("/WEB-INF/views/", ".jsp");
    }

    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        // 开启默认拦截器，不拦截静态资源
        // <mvc:default-servlet-handler />
        configurer.enable();
    }
}
