package cn.sjsdfg.config;

import cn.sjsdfg.web.http.converter.properties.PropertiesHttpMessageConverter;
import cn.sjsdfg.web.method.support.PropertiesHandlerMethodArgumentResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Joe on 2019/4/3.
 */
@Configuration
public class RestMvcConfig implements WebMvcConfigurer {
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

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(new PropertiesHttpMessageConverter());
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        // 错误实现： 因为内建 Converter 位置在自定义实现之前
        // 因而会被其他数据类型进行拦截，从而导致无法正确进行 Properties 的属性转换
//        resolvers.add(new PropertiesHandlerMethodArgumentResolver());
    }
}
