package cn.sjsdfg.servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by Joe on 2019/3/26.
 */
@WebServlet("/hello")
public class HelloServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println(Thread.currentThread() + " start .........");
        sayHello();
        System.out.println(Thread.currentThread() + " end .........");
        resp.getWriter().write("hello....");
    }

    private void sayHello() {
        System.out.println(Thread.currentThread() + " processing .........");
        try {
            Thread.sleep(3000);
        } catch (Exception e) {
        }
    }
}
