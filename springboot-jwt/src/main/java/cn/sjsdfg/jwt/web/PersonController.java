package cn.sjsdfg.jwt.web;

import cn.sjsdfg.jwt.bean.Person;
import cn.sjsdfg.jwt.bean.Role;
import cn.sjsdfg.jwt.service.PersonService;
import cn.sjsdfg.jwt.web.bean.ResponseResult;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Created by Joe on 2019/4/28.
 */
@RestController
public class PersonController {
    @Autowired
    private PersonService personService;

    @GetMapping(value = "/test")
    public String test(HttpServletRequest request) throws UnknownHostException {
        System.out.println(request.getRemoteAddr());
        System.out.println(request.getRemoteHost());
        System.out.println(InetAddress.getLocalHost().getHostAddress());
        return "";
    }

    @PostMapping(value = "/register", consumes = "application/json", produces = "application/json")
    public ResponseResult<Person> register(@RequestBody Person person) throws Exception {
        String username = person.getUsername();
        String password = person.getPassword();
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            throw new ServletException("Username or Password invalid!");
        }
        // 判断用户名是否已经被注册
        if (Objects.nonNull(personService.findByName(username))) {
            throw new ServletException("Username is used!");
        }
        // 设定用户默认权限 MEMBER
        List<Role> roles = new ArrayList<Role>();
        roles.add(Role.MEMBER);
        // 创建用户存储到 MongoDB 中
        person.setRoles(roles);
        person = personService.save(person);
        // 返回结果
        ResponseResult<Person> result = new ResponseResult<>();
        result.setData(person);
        result.setMessage("register success");
        result.setStatusCode("201 CREATED");
        return result;
    }

    @PostMapping("/login")
    public ResponseResult<String> login(@RequestBody Person person) throws Exception {
        String username = person.getUsername();
        String password = person.getPassword();
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            throw new ServletException("Username or Password invalid!");
        }
        person = personService.findByName(username);
        if (Objects.isNull(person) ||
                !person.getPassword().equals(password)) {
            throw new ServletException("Username or Password invalid!");
        }
        // 创建 JWT Token
        String jwtToken = Jwts.builder().setSubject(username)
                .claim("roles", "member")
                .setIssuedAt(new Date())
                .signWith(SignatureAlgorithm.HS256, "secretKey")
                .compact();
        ResponseResult<String> result = new ResponseResult<>();
        result.setStatusCode("200 OK");
        result.setMessage("login success");
        result.setData(jwtToken);
        return result;
    }
}
