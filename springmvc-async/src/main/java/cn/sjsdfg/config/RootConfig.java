package cn.sjsdfg.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.FilterType;
import org.springframework.stereotype.Controller;

/**
 * Created by Joe on 2019/3/26.
 * Spring 父容器不扫描 Controller
 */
@ComponentScan(value = "cn.sjsdfg", excludeFilters = {
        @Filter(type= FilterType.ANNOTATION, classes = {Controller.class})
})
public class RootConfig {
}
