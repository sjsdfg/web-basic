package cn.sjsdfg.servlet;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by Joe on 2019/3/27.
 */
@WebServlet(value = "/async", asyncSupported = true)
public class HelloAsyncServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println(Thread.currentThread() + " 主线程开始........");
        // 1. 开启异步处理 asyncSupported = true
        // 2. 开启异步模式
        AsyncContext asyncContext = req.startAsync();

        // 3. 业务逻辑开始进行异步处理
        asyncContext.start(() -> {
            try {
                sayHello();
                asyncContext.complete();
                // 4. 获取异步响应
                ServletResponse response = asyncContext.getResponse();
                response.getWriter().write("hello async....");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        System.out.println(Thread.currentThread() + " 主线程结束........");
    }

    private void sayHello() {
        System.out.println(Thread.currentThread() + " processing .........");
        try {
            Thread.sleep(3000);
        } catch (Exception e) {
        }
    }
}
