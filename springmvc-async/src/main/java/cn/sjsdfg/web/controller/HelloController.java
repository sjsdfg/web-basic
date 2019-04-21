package cn.sjsdfg.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Created by Joe on 2019/3/27.
 */
@Controller
public class HelloController {
    @GetMapping("/success")
    public String success() {
        return "success";
    }
}
