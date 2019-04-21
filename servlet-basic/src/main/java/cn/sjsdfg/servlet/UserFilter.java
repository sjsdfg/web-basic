package cn.sjsdfg.servlet;

import javax.servlet.*;
import java.io.IOException;

/**
 * Created by Joe on 2019/3/26.
 */
public class UserFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        // 用于过滤请求
        System.out.println("UserFilter..");
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {

    }
}
