package cn.sjsdfg.servlet;

import cn.sjsdfg.service.HelloService;

import javax.servlet.*;
import javax.servlet.annotation.HandlesTypes;
import java.util.EnumSet;
import java.util.Set;

/**
 * Created by Joe on 2019/3/26.
 */
//容器启动的时候会将 @HandlesTypes 所指定的这个类型下面的`子类/实现类`全部传递过来
@HandlesTypes(value = {HelloService.class})
public class MyServletContainerInitializer implements ServletContainerInitializer {
    /**
     *
     * @param set 为 HandlesTypes 里面所指定的所有感兴趣的子类型
     * @param servletContext 代表当前 web 应用的 ServletContext：一个 web 应用代表一个 web ServletContext
     * @throws ServletException
     */
    @Override
    public void onStartup(Set<Class<?>> set, ServletContext servletContext) throws ServletException {
        System.out.println("感兴趣的类型，为 cn.sjsdfg.service.HelloService 子类及其实现类");
        set.forEach(System.out::println);

        // 注册组件
        ServletRegistration.Dynamic userServlet = servletContext.addServlet("userServlet", new UserServlet());
        // 配置映射信息
        userServlet.addMapping("/user");

        // 注册 Listener
        servletContext.addListener(UserListener.class);

        // 注册 Filter
        FilterRegistration.Dynamic userFilter = servletContext.addFilter("userFilter", new UserFilter());
        // 配置 Filter 的映射信息
        userFilter.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, "/*");
    }
}
