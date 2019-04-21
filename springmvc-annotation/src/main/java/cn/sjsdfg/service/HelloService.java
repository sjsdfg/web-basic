package cn.sjsdfg.service;

import org.springframework.stereotype.Service;

/**
 * Created by Joe on 2019/3/27.
 */
@Service
public class HelloService {
    public String sayHello(String name) {
        return "hello" + name;
    }
}
