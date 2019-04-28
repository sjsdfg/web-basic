package cn.sjsdfg.jwt.web;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Joe on 2019/4/28.
 */
@RestController
@RequestMapping("/secure")
public class SecureController {
    @RequestMapping("/users/user")
    public String loginSuccess() {
        return "Login Successful!";
    }
}
