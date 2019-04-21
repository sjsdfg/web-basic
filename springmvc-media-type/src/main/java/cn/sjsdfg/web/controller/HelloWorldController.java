package cn.sjsdfg.web.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Properties;

/**
 * Created by Joe on 2019/4/3.
 */
@RestController
public class HelloWorldController {
    @GetMapping("hello")
    public String hello() {
        return "hello";
    }

    @PostMapping(value = "props", consumes = "text/properties", produces = "text/properties")
    public Properties props(@RequestBody Properties properties) {
        properties.forEach((key, value) -> {
            System.out.println(key + ":" + value);
        });
        return properties;
    }

    @PostMapping(value = "propsNo", consumes = "text/properties", produces = "text/properties")
    public Properties propsNoAnnotation(Properties properties) {
        properties.forEach((key, value) -> {
            System.out.println(key + ":" + value);
        });
        return properties;
    }
}
