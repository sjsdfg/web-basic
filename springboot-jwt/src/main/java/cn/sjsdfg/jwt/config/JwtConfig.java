package cn.sjsdfg.jwt.config;

import cn.sjsdfg.jwt.web.filter.JwtFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by Joe on 2019/4/28.
 * 针对 /secure/* 路径添加 JwtFilter
 */
@Configuration
public class JwtConfig {
    @Bean
    public FilterRegistrationBean jwtFilter() {
        final FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        registrationBean.setFilter(new JwtFilter());
        registrationBean.addUrlPatterns("/secure/*");
        return registrationBean;
    }

}
