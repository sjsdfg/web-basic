package cn.sjsdfg.servlet;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * 监听项目的启动和停止
 * Created by Joe on 2019/3/26.
 */
public class UserListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        System.out.println("UserListener ServletContextEvent contextInitialized....");
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        System.out.println("UserListener ServletContextEvent contextDestroyed....");
    }
}
