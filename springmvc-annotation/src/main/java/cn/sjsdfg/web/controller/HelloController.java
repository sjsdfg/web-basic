package cn.sjsdfg.web.controller;

import cn.sjsdfg.service.HelloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by Joe on 2019/3/27.
 */
@Controller
public class HelloController {
    @Autowired
    private HelloService helloService;

    @ResponseBody
    @GetMapping("/hello")
    public String hello() {
        return helloService.sayHello("tomcat");
    }

    @GetMapping("/success")
    public String success() {
        return "success";
    }
}
