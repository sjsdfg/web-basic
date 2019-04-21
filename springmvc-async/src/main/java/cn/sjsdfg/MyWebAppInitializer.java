package cn.sjsdfg;

import cn.sjsdfg.config.AppConfig;
import cn.sjsdfg.config.RootConfig;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

/**
 * Created by Joe on 2019/3/26.
 * web 容器创建的时候创建对象：调用方法来初始化前端控制器
 */
public class MyWebAppInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {
    /**
     * 获取根容器的配置类（Spring的配置文件） 父容器
     * @return
     */
    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class[]{RootConfig.class};
    }

    /**
     * 获取 web 容器的配置类（SpringMVC 配置文件）子容器
     * @return
     */
    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class[]{AppConfig.class};
    }

    /**
     * 获取 DispatcherServlet 的映射信息
     *   /   :拦截所有请求（包括静态资源 xx.js xx.png），但是不包括 *.jsp
     *   /*  :拦截所有请求，包括 *.jsp
     * @return
     */
    @Override
    protected String[] getServletMappings() {
        return new String[] {"/"};
    }
}
